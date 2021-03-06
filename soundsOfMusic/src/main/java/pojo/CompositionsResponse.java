package pojo;

import java.util.List;

public class CompositionsResponse {
    public final List<Composition> compositions;

    public CompositionsResponse(List<Composition> compositions) {
        this.compositions = compositions;
    }

    public Composition getById(int id) {
        return compositions.get(id);
    }
}
