package com.api.elnacional.components;

import com.api.elnacional.domain.Noticia;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class NoticiaListWrapper {
    @XmlElement(name = "noticia")
    private List<Noticia> noticias;

    public NoticiaListWrapper(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    public NoticiaListWrapper() {}
}