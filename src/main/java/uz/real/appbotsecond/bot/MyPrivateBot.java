//package uz.real.appbotsecond.bot;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import uz.real.appbotsecond.model.User;
//import uz.real.appbotsecond.repository.UserRepository;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.regex.Pattern;
//
//@Component
//public class MyPrivateBot extends TelegramLongPollingBot {
//
//    @Value("${telegram.bot.username}")
//    private String botUsername;
//
//    @Value("${telegram.bot.token}")
//    private String botToken;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage()) {
//            if (update.getMessage().hasText()) {
//                String fullName = "";
//                String regex = "^[a-zA-Z][ ]*$";
//                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//                LocalDate localDate = LocalDate.now();
//                String localDateString = localDate.toString();
//                System.out.println(localDate);
//
//                if (update.getMessage().getText().equalsIgnoreCase("/start") || update.getMessage().getText().equalsIgnoreCase("/")) {
//                    try {
//                        execute(showRegister(update.getMessage().getChatId()));
//                    } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                    }
//                } else if (update.getMessage().getText().matches("^[a-zA-z ]*$")) {
//                    fullName = update.getMessage().getText();
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setChatId(update.getMessage().getChatId());
//                    sendMessage.setText("Tug'ilgan sanasini kiriitng! Quyidagi formatda kiriting! (dd-MM-yyyy)");
//                    System.out.println(fullName);
//                    try {
//                        execute(sendMessage);
//                    } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                    }
//                } else if (update.getMessage().getText().matches("(0[1-9]|[12][0-9]|[3][01])-(0[1-9]|1[012])-\\\\d{4}\n") &&
//                    Integer.parseInt(update.getMessage().getText().substring(0, 4)) <= Integer.parseInt(localDateString.substring(0, 4))) {
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setChatId(update.getMessage().getChatId());
//                    sendMessage.setText("Do'stingizni tug'ilgan kuni muvafaqqiyatli saqlandi, botning o'zi sizga xabar yuboradi!");
//                    org.telegram.telegrambots.meta.api.objects.User user = update.getMessage().getFrom();
//                    User userFromDB = userRepository.getByUsername(user.getUserName());
//                    userFromDB.setFullName(fullName);
//                    userFromDB.setBirthDate(LocalDate.parse(update.getMessage().getText()));
//                    userRepository.save(userFromDB);
//                    try {
//                        execute(sendMessage);
//                    } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } else if (update.hasCallbackQuery()) {
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            long chatId = update.getCallbackQuery().getMessage().getChat().getId();
//            if (callbackQuery.getData().equalsIgnoreCase("A'zo bo'lish")) {
//                SendMessage shareContact = getShareContact(callbackQuery, callbackQuery.getMessage().getChat().getId());
//                try {
//                    execute(shareContact);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            } else if (callbackQuery.getData().equalsIgnoreCase("Yaqin insoningizni tug'ilgan sanasini saqlang!")) {
//                SendMessage sendMessage = new SendMessage();
//                sendMessage.setChatId(chatId);
//                sendMessage.setText("Ismi va familyasini kiriting! masalan:  Sherzod Nurmatov ko'rinishida!");
//                try {
//                    execute(sendMessage);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private SendMessage showRegister(long chatId) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//        inlineKeyboardButton.setText("A'zo bo'lish");
//        inlineKeyboardButton.setCallbackData("A'zo bo'lish");
//        List<InlineKeyboardButton> keyboardButtonList = new LinkedList<>();
//        keyboardButtonList.add(inlineKeyboardButton);
//        // row inline
//        List<List<InlineKeyboardButton>> inlineRows = new LinkedList<>();
//        inlineRows.add(keyboardButtonList);
//        inlineKeyboardMarkup.setKeyboard(inlineRows);
//        return new SendMessage()
//                .setChatId(chatId)
//                .setText("A'zo bo'lish tugmachasini bosing!")
//                .setReplyMarkup(inlineKeyboardMarkup);
//    }
//
//    private SendMessage getShareContact(CallbackQuery callbackQuery, long chatId) {
//        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//        inlineKeyboardButton.setText("Yaqin insoningizni tug'ilgan sanasini saqlang!");
//        inlineKeyboardButton.setCallbackData("Yaqin insoningizni tug'ilgan sanasini saqlang!");
//        List<InlineKeyboardButton> keyboardButtonList = new LinkedList<>();
//        keyboardButtonList.add(inlineKeyboardButton);
//        List<List<InlineKeyboardButton>> inlineRows = new LinkedList<>();
//        inlineRows.add(keyboardButtonList);
//        keyboardMarkup.setKeyboard(inlineRows);
//        org.telegram.telegrambots.meta.api.objects.User from = callbackQuery.getFrom();
//        Optional<User> byUsername = userRepository.findByUsername(from.getUserName());
//        if (byUsername.isPresent()) {
//            return new SendMessage().setChatId(chatId)
//                    .setText("Siz avval ushbu botga a'zo bo'lgansiz!")
//                    .setReplyMarkup(keyboardMarkup);
//        }
//        User user = new User();
//        user.setFirstName(from.getFirstName());
//        user.setUsername(from.getUserName());
//        user.setChatId(from.getId().toString());
//        userRepository.save(user);
//        return new SendMessage().setChatId(chatId)
//                .setText("Tabriklaymiz siz ushbu botga muvafaqqiyatli a'zo bo'ldingiz!")
//                .setReplyMarkup(keyboardMarkup);
//    }
//
//    private SendMessage selectMonth(long chatId) {
//        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
//
//        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//        inlineKeyboardButton.setText("Jan");
//        inlineKeyboardButton.setCallbackData("Jan");
//
//        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
//        inlineKeyboardButton1.setText("Feb");
//        inlineKeyboardButton1.setCallbackData("Feb");
//
//        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
//        inlineKeyboardButton2.setText("March");
//        inlineKeyboardButton2.setCallbackData("March");
//
//        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
//        inlineKeyboardButton3.setText("Apr");
//        inlineKeyboardButton3.setCallbackData("Apr");
//
//        List<InlineKeyboardButton> keyboardButtonList1 = new LinkedList<>();
//        keyboardButtonList1.add(inlineKeyboardButton);
//        keyboardButtonList1.add(inlineKeyboardButton1);
//        keyboardButtonList1.add(inlineKeyboardButton2);
//        keyboardButtonList1.add(inlineKeyboardButton3);
//
//        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
//        inlineKeyboardButton4.setText("May");
//        inlineKeyboardButton4.setCallbackData("May");
//
//        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
//        inlineKeyboardButton5.setText("June");
//        inlineKeyboardButton5.setCallbackData("June");
//
//        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
//        inlineKeyboardButton6.setText("July");
//        inlineKeyboardButton6.setCallbackData("July");
//
//        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
//        inlineKeyboardButton7.setText("Aug");
//        inlineKeyboardButton7.setCallbackData("Aug");
//
//        List<InlineKeyboardButton> keyboardButtonList2 = new LinkedList<>();
//        keyboardButtonList2.add(inlineKeyboardButton4);
//        keyboardButtonList2.add(inlineKeyboardButton5);
//        keyboardButtonList2.add(inlineKeyboardButton6);
//        keyboardButtonList2.add(inlineKeyboardButton7);
//
//        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton();
//        inlineKeyboardButton8.setText("Sep");
//        inlineKeyboardButton8.setCallbackData("Sep");
//
//        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton();
//        inlineKeyboardButton9.setText("Oct");
//        inlineKeyboardButton9.setCallbackData("Oct");
//
//        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton();
//        inlineKeyboardButton10.setText("Nov");
//        inlineKeyboardButton10.setCallbackData("Nov");
//
//        InlineKeyboardButton inlineKeyboardButton11 = new InlineKeyboardButton();
//        inlineKeyboardButton11.setText("Dec");
//        inlineKeyboardButton11.setCallbackData("Dec");
//
//        List<InlineKeyboardButton> keyboardButtonList3 = new LinkedList<>();
//        keyboardButtonList3.add(inlineKeyboardButton8);
//        keyboardButtonList3.add(inlineKeyboardButton9);
//        keyboardButtonList3.add(inlineKeyboardButton10);
//        keyboardButtonList3.add(inlineKeyboardButton11);
//
//        List<List<InlineKeyboardButton>> inlineRows = new LinkedList<>();
//
//        inlineRows.add(keyboardButtonList1);
//        inlineRows.add(keyboardButtonList2);
//        inlineRows.add(keyboardButtonList3);
//        keyboardMarkup.setKeyboard(inlineRows);
//        return new SendMessage()
//                .setChatId(chatId)
//                .setText("Select month!")
//                .setReplyMarkup(keyboardMarkup);
//    }
//
//}
