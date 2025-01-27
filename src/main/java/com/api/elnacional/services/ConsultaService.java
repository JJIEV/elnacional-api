package com.api.elnacional.services;

import com.api.elnacional.domain.Noticia;
import com.api.elnacional.repository.ParametroRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ConsultaService {


    private static final Logger logger = LoggerFactory.getLogger(ConsultaService.class);

    @Autowired
    ParametroRepository parametroRepository;

    public List<Noticia> buscarNoticias(String query, boolean includeFoto) throws Exception {

        String urlElNacional = parametroRepository.findValorByNombre("URL_WEB");

        RestTemplate restTemplate = new RestTemplate();
        String url = urlElNacional + "?s=" + query;
        Document document = Jsoup.connect(url)
                .timeout(30000)
                .get();

        List<Noticia> noticias = new ArrayList<>();
        Elements resultados = document.select("li.mvp-blog-story-wrap");

        for (Element elemento : resultados) {
            Noticia noticia = new Noticia();
            noticia.setTitulo(elemento.select("h2").text());
            noticia.setEnlace(elemento.select("a").attr("href"));
            noticia.setFecha(elemento.select(".mvp-cd-date").text());
            noticia.setResumen(elemento.select("p").text());

            String enlaceFoto = elemento.select(".mvp-blog-story-img img").attr("src");
            noticia.setEnlaceFoto(enlaceFoto);

            if (includeFoto && enlaceFoto != null && !enlaceFoto.isEmpty()) {
                try {
                    byte[] imageBytes = restTemplate.getForObject(enlaceFoto, byte[].class);
                    noticia.setContenidoFoto(Base64.getEncoder().encodeToString(imageBytes));

                    HttpHeaders headers = restTemplate.headForHeaders(enlaceFoto);
                    noticia.setContentTypeFoto(headers.getContentType().toString());
                } catch (Exception e) {
                    logger.warn("Error descargando la imágen: " + enlaceFoto, e);
                }
            }

            noticias.add(noticia);
        }
        return noticias;
    }
}