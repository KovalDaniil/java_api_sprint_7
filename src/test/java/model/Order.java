package model;

import java.util.List;

// модель заказа
public class Order {
    private Long id;
    private String comment;
    private List<String> colors;
    private String deliveryDate;
    private Integer weight;
    private String track; // трек номер приходит только в ответе
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public List<String> getColors() {
        return colors;
    }
    public void setColors(List<String> colors) {
        this.colors = colors;
    }
    public String getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public String getTrack() {
        return track;
    }
    public void setTrack(String track) {
        this.track = track;
    }
}