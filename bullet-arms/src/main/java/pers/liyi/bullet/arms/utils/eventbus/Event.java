package pers.liyi.bullet.arms.utils.eventbus;


public class Event<K,V> {
    private K key;
    private V value;

    public Event() {

    }

    public Event(K key){
        this.key=key;
    }

    public Event(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
