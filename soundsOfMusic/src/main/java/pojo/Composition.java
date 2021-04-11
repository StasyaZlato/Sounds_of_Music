package pojo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = CompositionDeserializer.class)
public class Composition {
    private String filename;
    private int sr;
    private double duration;
    private List<CompositionChord> chords;

    public Composition() { }

    public Composition(
            String filename,
            int sr,
            double duration,
            List<CompositionChord> chords) {
        this.filename = filename;
        this.sr = sr;
        this.duration = duration;
        this.chords = chords;
    }

    public double getDuration() {
        return duration;
    }

    public int getSr() {
        return sr;
    }

    public List<CompositionChord> getChords() {
        return chords;
    }

    public String getFilename() {
        return filename;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSr(int sr) {
        this.sr = sr;
    }

    public void setChords(List<CompositionChord> chords) {
        this.chords = chords;
    }
}

class CompositionDeserializer extends StdDeserializer<Composition> {

    public CompositionDeserializer() {
        this(null);
    }

    public CompositionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Composition deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {

        JsonNode compositionNode = jp.getCodec().readTree(jp);
        Composition composition = new Composition();

        composition.setSr(compositionNode.get("sr").intValue());
        composition.setDuration(compositionNode.get("duration").doubleValue());
        composition.setFilename(compositionNode.get("filename").textValue());

        List<CompositionChord> chords = new ArrayList<>();

        JsonNode chordsNode = compositionNode.get("chords");
        for (var el: chordsNode) {
            chords.add(new CompositionChord(el.get("duration").doubleValue(), el.get("chord").textValue()));
        }

        composition.setChords(chords);

        return composition;
    }
}

