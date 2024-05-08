package br.com.rodjrm.ScreenMatch.principal;

import br.com.rodjrm.ScreenMatch.model.DadosEpisodio;
import br.com.rodjrm.ScreenMatch.model.DadosSerie;
import br.com.rodjrm.ScreenMatch.model.DadosTemporada;
import br.com.rodjrm.ScreenMatch.service.ConsumoApi;
import br.com.rodjrm.ScreenMatch.service.ConverteDados;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    }
}
