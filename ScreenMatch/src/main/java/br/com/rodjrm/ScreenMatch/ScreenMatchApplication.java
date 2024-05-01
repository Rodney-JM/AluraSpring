package br.com.rodjrm.ScreenMatch;

import br.com.rodjrm.ScreenMatch.model.DadosEpisodio;
import br.com.rodjrm.ScreenMatch.model.DadosSerie;
import br.com.rodjrm.ScreenMatch.model.DadosTemporada;
import br.com.rodjrm.ScreenMatch.service.ConsumoApi;
import br.com.rodjrm.ScreenMatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoApi consumoApi = new ConsumoApi();
		var json = consumoApi.obtemDados("http://www.omdbapi.com/?t=one+piece&apikey=4e1d39e5");
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
		json = consumoApi.obtemDados("http://www.omdbapi.com/?t=one+piece&season=1&episode=1&apikey=4e1d39e5");
		DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);

		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i=1;i<=dados.totalTemporadas();i++){
			json = consumoApi.obtemDados("http://www.omdbapi.com/?t=one+piece&season="+i+"&apikey=4e1d39e5");
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);
	}
}
