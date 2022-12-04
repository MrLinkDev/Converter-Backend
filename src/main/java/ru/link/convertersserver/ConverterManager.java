package ru.link.convertersserver;

import org.springframework.stereotype.Service;

@Service
public class ConverterManager {
    private final int CONVERTER_COUNT = 4;
    private final Converter[] converters = new Converter[CONVERTER_COUNT];

    public ConverterManager() {
        for (int id = 0; id < CONVERTER_COUNT; ++id) {
            converters[id] = new Converter(id);
        }
    }

    /**
     * @param id Идентификатор конвертера
     * @param address IP-адресс конвертера
     * @return В случае успешного подключения, возвращает ответ на команду INIT?.
     *         В противном случае, возвращает "No connection"
     */
    public String connect(int id, String address) {
        return converters[id].connect(address);
    }

    /**
     * @param id Идентификатор конвертера
     * @return Возвращает true в случае успешного закрытия соединения.
     *         В противном случае - false.
     */
    public boolean disconnect(int id) {
        return converters[id].disconnect();
    }

    /**
     * @param id Идентификатор конвертера
     * @param cmd Команда, отправляемая в конвертер
     * @return Возвращает ответ на команду. Если соединение с конвертером было
     *         потеряно, возвращает "No connection".
     */
    public String sendCommand(int id, String cmd) {
        return converters[id].sendCommand(cmd);
    }

    /**
     * @param id Идентификатор конвертера
     * @return Возвращает значение температуры. Если соединение с конвертером было
     *         потеряно, возвращает "No connection".
     */
    public String getTemp(int id) {
        return converters[id].getTemp();
    }
}
