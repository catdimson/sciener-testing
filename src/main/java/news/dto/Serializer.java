package news.dto;

/**
 * Базовый интерфейс для сериализации
 */
public interface Serializer<T> {

    /**
     * Сериализация в строку JSON
     */
    public String toJSON() throws ClassNotFoundException;

    /**
     * Сериализация строки JSON в объект доменной модели
     */
    public T toObject();

}
