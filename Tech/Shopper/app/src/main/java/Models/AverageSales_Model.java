package Models;

import java.util.List;

public class AverageSales_Model {
    String dateSold;
    List<String> revenue;
    List<String> timeslot;
    String uid;

    public AverageSales_Model(){}
    public AverageSales_Model(String dateSold, List<String> revenue, List<String> timeslot, String uid) {
        this.dateSold = dateSold;
        this.revenue = revenue;
        this.timeslot = timeslot;
        this.uid = uid;
    }

    public String getDateSold() {
        return dateSold;
    }

    public void setDateSold(String dateSold) {
        this.dateSold = dateSold;
    }

    public List<String> getRevenue() {
        return revenue;
    }

    public void setRevenue(List<String> revenue) {
        this.revenue = revenue;
    }

    public List<String> getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(List<String> timeslot) {
        this.timeslot = timeslot;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
