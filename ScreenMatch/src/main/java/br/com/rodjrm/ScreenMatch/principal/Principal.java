package br.com.rodjrm.ScreenMatch.principal;

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
    }
}
