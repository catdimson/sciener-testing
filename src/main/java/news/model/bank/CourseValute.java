package news.model.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CourseValute {

    @JsonProperty("Date")
    private String date;
    @JsonProperty("PreviousDate")
    private String previousDate;
    @JsonProperty("PreviousURL")
    private String previousURL;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("Valute")
    private Valutes valutes;

    public Valutes getValutes() {
        return this.valutes;
    }

    @Override
    public String toString() {
        return "Valutes{" +
                "date='" + date + '\'' +
                ", previousDate='" + previousDate + '\'' +
                ", previousURL='" + previousURL + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", valutes=" + valutes +
                '}';
    }
}