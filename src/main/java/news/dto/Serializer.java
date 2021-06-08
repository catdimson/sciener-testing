package news.dto;

/**
 * Базовый интерфейс для сериализации
 */
@Deprecated
public interface Serializer<T> {

    /**
     * Сериализация в строку JSON
     */
    String toJSON() throws ClassNotFoundException;

    /**
     * Сериализация строки JSON в объект доменной модели
     */
    T toObject();

}
