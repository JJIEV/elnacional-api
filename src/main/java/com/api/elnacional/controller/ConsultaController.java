package com.api.elnacional.controller;


import com.api.elnacional.components.NoticiaListWrapper;
import com.api.elnacional.domain.Noticia;
import com.api.elnacional.repository.ParametroRepository;
import com.api.elnacional.services.ApiKeyService;
import com.api.elnacional.services.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/consulta")
public class ConsultaController {
    private static final Logger logger = LoggerFactory.getLogger(ConsultaController.class);

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    ParametroRepository parametroRepository;

    @GetMapping(value = "/buscar-noticias",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.TEXT_PLAIN_VALUE,
                    MediaType.TEXT_HTML_VALUE
            })
    public ResponseEntity<?> buscarNoticias(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "false") boolean f,
            @RequestHeader(required = false) String apiKey,
            @RequestHeader(required = false) String firma,
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader) {

        if (!apiKeyService.validateApiKey(apiKey, firma)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("codigo", "g103", "error", "No autorizado"));
        }

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("codigo", "g268", "error", "Parámetros inválidos"));
        }

        try {
            List<Noticia> noticias = consultaService.buscarNoticias(q, f);

            if (noticias.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", "g267",
                                "error", "No se encuentran noticias para el texto: " + q));
            }

            Object response = switch (acceptHeader) {
                case MediaType.APPLICATION_XML_VALUE -> new NoticiaListWrapper(noticias);
                case MediaType.TEXT_PLAIN_VALUE -> noticias.stream()
                        .map(Noticia::toString)
                        .collect(Collectors.joining("\n"));
                case MediaType.TEXT_HTML_VALUE -> generateHtml(noticias);
                default -> noticias;
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(acceptHeader))
                    .body(response);

        } catch (Exception e) {
            logger.error("Error buscando noticias", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("codigo", "g100",
                            "error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    private String generateHtml(List<Noticia> noticias) {
        return noticias.stream()
                .map(n -> String.format("""
                                <div class='noticia'>
                                    <h2>%s</h2>
                                    <p>%s</p>
                                    <p>Fecha: %s</p>
                                    <a href='%s'>Leer más</a>
                                    %s
                                </div>
                                """,
                        n.getTitulo(),
                        n.getResumen(),
                        n.getFecha(),
                        n.getEnlace(),
                        n.getContenidoFoto() != null ?
                                "<img src='data:" + n.getContentTypeFoto() + ";base64," +
                                        n.getContenidoFoto() + "'/>" : ""))
                .collect(Collectors.joining("",
                        "<html><body>",
                        "</body></html>"));
    }


    @GetMapping("/generar-firma")
    public Map<String, String> generateSignature() {
        String apiKey = parametroRepository.findValorByNombre("API_KEY");
        String signature = apiKeyService.generateSignature(apiKey);
        return Map.of(
                "apiKey", apiKey,
                "firma", signature
        );
    }

}
