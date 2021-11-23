package uz.real.appbotsecond.bot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.real.appbotsecond.model.BirthDay;
import uz.real.appbotsecond.repository.BirthDayRepository;
import uz.real.appbotsecond.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class MyConfigurationBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BirthDayRepository birthDayRepository;

    private String fullName = "";
    LocalDate budilnikDate;

    @Override
    public void onUpdateReceived(Update update) {
        String regex = "^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$";
        Pattern pattern = Pattern.compile(regex);

        if (update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            if (message.hasText()) {
                String messageText = message.getText();
                if (messageText.equalsIgnoreCase("/start") || message.getText().equalsIgnoreCase("/")) {
                    SendMessage sendMessage = useBot(chatId);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (messageText.matches("^[a-zA-z ]*$")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Do'stingizning tug'ilgan sanasini kiriting! dd/MM/yyyy ko'rinishida!");
                        fullName = messageText;
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (pattern.matcher(messageText).matches()) {
                        SendMessage sendMessage = checkDate(messageText, message);
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();

            if (callbackQuery.getData().equalsIgnoreCase("Botdan foydalanish!")) {
                SendMessage sendMessage = saveUser(callbackQuery, callbackQuery.getMessage().getChat().getId());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (callbackQuery.getData().equalsIgnoreCase("Yana do'stlarni saqlash!")) {

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(callbackQuery.getMessage().getChat().getId());
                sendMessage.setText("Do'stingizning ismi va familyasini kiriting! masalan: Sherzod Nurmatov ko'rinishida");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private SendMessage useBot(long chatId) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Botdan foydalanish!");
        inlineKeyboardButton.setCallbackData("Botdan foydalanish!");
        List<InlineKeyboardButton> keyboardButtonList = new LinkedList<>();
        keyboardButtonList.add(inlineKeyboardButton);
        List<List<InlineKeyboardButton>> inlineRows = new LinkedList<>();
        inlineRows.add(keyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineRows);
        return new SendMessage()
                .setChatId(chatId)
                .setText("Assalomu alaykum, yaqinlaringizni tug'ilgan kunini saqlab boruvchi botga hush kelibsiz!")
                .setReplyMarkup(inlineKeyboardMarkup);
    }

    private SendMessage saveUser(CallbackQuery callbackQuery, long chatId) {
        User from = callbackQuery.getFrom();
        Optional<uz.real.appbotsecond.model.User> byUsername = userRepository.findByUsername(from.getUserName());
        if (byUsername.isPresent()) {
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Siz avval ham ushbu botdan foydalangansiz, marxamat do'stingizning ismi va familyasini kiriting!" +
                            "masalan Sherzod Nurmatov ko'rinishida");
        }
        uz.real.appbotsecond.model.User user = new uz.real.appbotsecond.model.User();
        user.setFirstName(from.getFirstName());
        user.setUsername(from.getUserName());
        user.setChatId(String.valueOf(chatId));
        userRepository.save(user);
        return new SendMessage()
                .setChatId(chatId)
                .setText("Do'stingizning ismi va familyasini kiriting masalan: Sherzod Nurmatov ko'rinishida!");
    }

    private SendMessage checkDate(String stringDate, Message message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Yana do'stlarni saqlash!");
        inlineKeyboardButton.setCallbackData("Yana do'stlarni saqlash!");
        List<InlineKeyboardButton> keyboardButtonList = new LinkedList<>();
        keyboardButtonList.add(inlineKeyboardButton);
        List<List<InlineKeyboardButton>> inlineRows = new LinkedList<>();
        inlineRows.add(keyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineRows);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int day = Integer.parseInt(stringDate.substring(0, 2));
        int month = Integer.parseInt(stringDate.substring(3, 5));
        int year = Integer.parseInt(stringDate.substring(6, stringDate.length()));
        LocalDate nowDate = LocalDate.now();
        if (day >= 1 && day <= 31) {
            if (month >= 1 && month <= 12) {
                if (year <= nowDate.getYear()) {
                    budilnikDate = LocalDate.parse(stringDate, formatter);
                    uz.real.appbotsecond.model.User user = userRepository.getByUsername(message.getFrom().getUserName());
                    BirthDay savedBirthDay = new BirthDay();
                    savedBirthDay.setDay(budilnikDate.getDayOfMonth());
                    savedBirthDay.setMonth(budilnikDate.getMonthValue());
                    savedBirthDay.setFullName(fullName);
                    savedBirthDay.setUser(user);
                    birthDayRepository.save(savedBirthDay);
                    return new SendMessage()
                            .setChatId(message.getChatId())
                            .setText("Tabriklaymiz siz do'stingizni tug'ilgan kunini muvafaqqiyatli saqladingiz, botning o'zi sizga xabar yuboradi!")
                            .setReplyMarkup(inlineKeyboardMarkup);
                } else {
                    return new SendMessage()
                            .setChatId(message.getChatId())
                            .setText("Yilni to'g'ri kiriting, yil 2022 dan kichik bo'lishi kerak. Siz " + year + " kiritdingiz!");
                }
            } else {
                return new SendMessage()
                        .setChatId(message.getChatId())
                        .setText("Oyni to'g'ri kiriting, oy (1-12) oralig'ida bo'lishi kerak! Siz " + month + " kiritdingiz!");
            }
        } else {
            return new SendMessage()
                    .setChatId(message.getChatId())
                    .setText("Kunni to'g'ri kiriting, kun (1-31) oralig'ida bo'lishi kerak! Siz " + day + " kiritdingiz!");
        }
    }

    @Scheduled(cron = "0 55 23 * * *")
    private void budilnik() {
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        SendMessage sendMessage = new SendMessage();

        List<BirthDay> selectBirthList = birthDayRepository.findAllByDayAndMonth(day, month);

        if (selectBirthList.size() > 0) {
            for (BirthDay birthDay : selectBirthList) {
                sendMessage.setChatId(birthDay.getUser().getChatId());
                sendMessage.setText("Bugun " + birthDay.getFullName() + " do'stingizning tug'ilgan kuni! tabriklab qo'yishni unutmang!");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Xabar yuborildi!");
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
