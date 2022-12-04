package ru.link.convertersserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class ConvertersServerController {
    private final int CONVERTER_COUNT = 4;

    private final ConverterManager converterManager;
    private final Object[] lock = new Object[CONVERTER_COUNT];


    public ConvertersServerController() {
        this.converterManager = new ConverterManager();

        for (int i = 0; i < CONVERTER_COUNT; ++i) {
            lock[i] = new Object();
        }
    }

    /**
     * @param id Идентификатор конвертера
     * @param address IP-адресс конвертера
     * @return В случае успешного подключения, возвращает ответ на команду INIT?.
     *         В противном случае, возвращает "No connection"
     */
    @PostMapping("/connect/{id}")
    public @ResponseBody String connect(@PathVariable("id") final int id, @ModelAttribute("address") String address) {
        synchronized (lock[id]) {
            return converterManager.connect(id, address);
        }
    }

    /**
     * @param id Идентификатор конвертера
     * @return Возвращает true в случае успешного закрытия соединения.
     *         В противном случае - false.
     */
    @GetMapping("/disconnect/{id}")
    public @ResponseBody boolean disconnect(@PathVariable("id") final int id) {
        return converterManager.disconnect(id);
    }

    /**
     * @param id Идентификатор конвертера
     * @param cmd Команда, отправляемая в конвертер
     * @return Возвращает ответ на команду. Если соединение с конвертером было
     *         потеряно, возвращает "No connection".
     */
    @PostMapping("/send_command/{id}")
    public @ResponseBody String sendCommand(@PathVariable("id") final int id, @ModelAttribute("cmd") String cmd) {
        synchronized (lock[id]) {
            return converterManager.sendCommand(id, cmd);
        }
    }

    /**
     * @param id Идентификатор конвертера
     * @return Возвращает значение температуры. Если соединение с конвертером было
     *         потеряно, возвращает "No connection".
     */
    @GetMapping("/get_temp/{id}")
    public @ResponseBody String getTemp(@PathVariable("id") final int id) {
        synchronized (lock[id]) {
            return converterManager.getTemp(id);
        }
    }
}
