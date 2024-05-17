package br.com.rodjrm.ScreenMatch.principal;

import br.com.rodjrm.ScreenMatch.model.DadosEpisodio;
import br.com.rodjrm.ScreenMatch.model.DadosSerie;
import br.com.rodjrm.ScreenMatch.model.DadosTemporada;
import br.com.rodjrm.ScreenMatch.model.Episodio;
import br.com.rodjrm.ScreenMatch.service.ConsumoApi;
import br.com.rodjrm.ScreenMatch.service.ConverteDados;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Principal {
    private Scanner in = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=4e1d39e5";
    public void exibeMenu() throws JsonProcessingException {
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = in.nextLine();
        var json = consumoApi.obtemDados(ENDERECO + nomeSerie.replaceAll(" ", "+") + API_KEY);

        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);

        System.out.println(dadosSerie);


        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i=1;i<=dadosSerie.totalTemporadas();i++){
            json = consumoApi.obtemDados(ENDERECO + nomeSerie.replaceAll(" ", "+") + "&season="+i+API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

//        for(int i = 0; i< dadosSerie.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j< episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        temporadas.forEach(t-> t.episodios().forEach(e -> System.out.println(e.titulo()))); // lambdas sao funcoes com parametros imbutidos(funcoes anonimas)

//        List<String> nomes = Arrays.asList("Rodney", "Paulo", "Jonas");
//
//        nomes.stream()
//                //operacoes intermediarias
//                .sorted()
//                .limit(2)
//                .filter(n -> n.startsWith("R"))
//                .map(n -> n.toUpperCase())
//                //operacoes finais
//                .forEach(System.out::println);

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("Top 5 episodios: ");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e-> System.out.println("Primeiro filtro(N/A)" + e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())//compara a avaliacao deles e retorna eles do maior para o menor
                .peek(e -> System.out.println("Ordenação " + e))
                .limit(5)
                .peek(e -> System.out.println("Limite " + e))
                .map(e-> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Ordenação " + e))
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e))).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo do episódio: ");
        var trechoTitulo = in.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()//Optional guarda os episódios em um container
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();//encontra o nome com base em uma mesma ordem
        if(episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: "+ episodioBuscado.get().getTemporada());
            System.out.println("Nome: "+ episodioBuscado.get().getTitulo());
        }else {
            System.out.println("Episódio não encontrado!");
        }

        System.out.println("A partir de que ano você deseja ver os episódios?");
        var ano = in.nextInt();
        in.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episodio: "+ e.getTitulo() +
                                " Data Lançamento: " + e.getDataLancamento().format(dtf)
                ));

        Map<Integer, Double> avaliacoesTemporadas = episodios.stream()
                .filter(e -> e.getAvaliacao()>0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));//Agrupando as avaliações por temporada

        System.out.println(avaliacoesTemporadas);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao()> 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));//cria uma coleção com as avaliacoes

        System.out.println("Média: "+est.getAverage());
        System.out.println("Melhor episódio: "+est.getMax());
        System.out.println("Pior episódio: "+est.getMin());
        System.out.println("Quantidade de episódios observados: "+est.getCount());
    }
}
