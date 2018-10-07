package xmlParsing;

/**
 * Created by George on 16/05/2018.
 */
public class Star {
    private String id;

    private String name;

    private String birthyear;

    public Star(){

    }

    public Star(String id, String name, String birthyear) {
        this.id = id;
        this.name = name;
        this.birthyear = birthyear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(String birthyear) {
        this.birthyear = birthyear;
    }

    @Override
    public String toString() {
        return "Star{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthyear='" + birthyear + '\'' +
                '}';
    }
}
