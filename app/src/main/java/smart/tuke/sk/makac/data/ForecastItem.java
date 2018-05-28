package smart.tuke.sk.makac.data;

public class ForecastItem {
    private int code;
    private String temp;
    private String text;
    private String city;

    public int getCode() {
        return code;
    }

    public String getTemperature() {
        return temp;
    }

    public String getText() {
        return text;
    }

    public String getCity() {
        return city;
    }

    public ForecastItem (int code, String temp, String text, String city) {
        this.code = code;
        this.temp = temp;
        this.text = text;
        this.city = city;
    }
}
