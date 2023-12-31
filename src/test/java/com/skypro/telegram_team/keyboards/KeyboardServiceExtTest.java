package com.skypro.telegram_team.keyboards;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.keyboards.buffers.Question;
import com.skypro.telegram_team.keyboards.buffers.QuestionsBuffer;
import com.skypro.telegram_team.keyboards.buffers.Request;
import com.skypro.telegram_team.keyboards.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.ReportService;
import com.skypro.telegram_team.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeyboardServiceExtTest {
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private UserService userService;
    @Mock
    private ReportService reportService;
    @Mock
    private RequestsBuffer requestsBuffer;
    @Mock
    private QuestionsBuffer questionsBuffer;

    @InjectMocks
    private KeyboardServiceExt out;

    @BeforeEach
    public void setUp() {
        out = new KeyboardServiceExt(telegramBot, userService, reportService, questionsBuffer, requestsBuffer);
        when(telegramBot.execute(any())).thenReturn(generateResponseOk());
    }

    //Тесты меню
    @ParameterizedTest
    @MethodSource("provideParamsForMenuTests")
    void processUpdateMenuItems(String menuText, String message) throws Exception {
        //Given
        Update update = generateUpdate(menuText);
        //When
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(message);
        Assertions.assertThat(actual.getParameters().get("reply_markup")).isNotNull();
    }

    static Stream<Arguments> provideParamsForMenuTests() {
        return Stream.of(Arguments.of(KeyboardServiceExt.Menu.START.getText(), "Привет!"),
                Arguments.of(KeyboardServiceExt.Menu.GET_INFO.getText(), "Информация о приюте"),
                Arguments.of(KeyboardServiceExt.Menu.GET_ANIMAL.getText(), "Как взять собаку"),
                Arguments.of(KeyboardServiceExt.Menu.SEND_REPORT.getText(), "Какие данные отправить?"),
                Arguments.of(KeyboardServiceExt.Menu.SET_USER_DATA.getText(), "Какие данные записать?"),
                Arguments.of(KeyboardServiceExt.Menu.ASK_VOLUNTEER.getText(), "Кого спросить?")
        );
    }

    //Тесты команд inline keyboard
    @ParameterizedTest
    @MethodSource("provideParamsForCallbackTests")
    void processUpdateCallbacks(String command, String message) throws Exception {
        //Given
        Update update = generateUpdateWithCallback(command);
        //When
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(message);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForVolunteersTests")
    void processUpdateCallbacksForVolunteer(String command, String message, boolean searchVolunteer, boolean noFree) throws Exception {
        //Given
        Update update = generateUpdateWithCallback(command);
        //When
        if (searchVolunteer) {
            if (!noFree) {
                User volunteer = new User();
                volunteer.setTelegramId(123);
                when(userService.findAnyVolunteer()).thenReturn(Optional.of(volunteer));
            } else {
                when(userService.findAnyVolunteer()).thenReturn(Optional.empty());
            }
        }
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(message);
    }

    static Stream<Arguments> provideParamsForCallbackTests() {
        return Stream.of(Arguments.of(KeyboardServiceExt.Command.INF_ADDRESS.name(), Shelter.getAddress()),
                Arguments.of(KeyboardServiceExt.Command.INF_SCHEDULE.name(), Shelter.getSchedule()),
                Arguments.of(KeyboardServiceExt.Command.INF_SCHEME.name(), Shelter.getScheme()),
                Arguments.of(KeyboardServiceExt.Command.INF_SAFETY.name(), Shelter.getSafety()),
                Arguments.of(KeyboardServiceExt.Command.HOW_RULES.name(), Shelter.getRules()),
                Arguments.of(KeyboardServiceExt.Command.HOW_DOCS.name(), Shelter.getDocs()),
                Arguments.of(KeyboardServiceExt.Command.HOW_MOVE.name(), Shelter.getMove()),
                Arguments.of(KeyboardServiceExt.Command.HOW_ARRANGE.name(), Shelter.getArrangements()),
                Arguments.of(KeyboardServiceExt.Command.HOW_ARRANGE_PUPPY.name(), Shelter.getArrangementsForPuppy()),
                Arguments.of(KeyboardServiceExt.Command.HOW_ARRANGE_CRIPPLE.name(), Shelter.getArrangementsForCripple()),
                Arguments.of(KeyboardServiceExt.Command.HOW_EXPERT_FIRST.name(), Shelter.getExpertAdvicesFirst()),
                Arguments.of(KeyboardServiceExt.Command.HOW_EXPERT_NEXT.name(), Shelter.getExpertAdvicesNext()),
                Arguments.of(KeyboardServiceExt.Command.HOW_REJECT_REASONS.name(), Shelter.getRejectReasons()),
                Arguments.of(KeyboardServiceExt.Command.SAVE_USER_PHONE.name(), "Напишите телефон"),
                Arguments.of(KeyboardServiceExt.Command.SAVE_USER_EMAIL.name(), "Напишите почту"),
                Arguments.of(KeyboardServiceExt.Command.SEND_PHOTO.name(), "Отправьте фото"),
                Arguments.of(KeyboardServiceExt.Command.SEND_DIET.name(), "Опишите диету"),
                Arguments.of(KeyboardServiceExt.Command.SEND_BEHAVIOR.name(), "Опишите поведение"),
                Arguments.of(KeyboardServiceExt.Command.SEND_WELL_BEING.name(), "Опишите самочувствие")
        );
    }

    static Stream<Arguments> provideParamsForVolunteersTests() {
        return Stream.of(Arguments.of(KeyboardServiceExt.Command.ASK_VOLUNTEER.name() + "123", "Напишите вопрос", false, false),
                Arguments.of(KeyboardServiceExt.Command.ASK_ANY_VOLUNTEER.name(), "Напишите вопрос", true, false),
                Arguments.of(KeyboardServiceExt.Command.ASK_ANY_VOLUNTEER.name(), "Нет свободных волонтеров", true, true)
        );
    }

    //Тесты сохранения данных
    @Test
    void processUpdateUserSavePhone() throws Exception {
        //Given
        Update update = generateUpdate("+7");
        Request request = new Request(update.message().chat().id());
        request.setUserPhoneRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(userService.update(any(), any())).thenReturn(user);
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Данные пользователя записаны");
    }

    @Test
    void processUpdateUserSaveWithException() throws Exception {
        //Given
        Update update = generateUpdate("11@ru");
        Request request = new Request(update.message().chat().id());
        request.setUserEmailRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(userService.update(any(), any())).thenThrow(new InvalidDataException("error"));
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Возникла ошибка: error");
    }

    @Test
    void processUpdateReportSaveDiet() throws Exception {
        //Given
        Update update = generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportDietRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenReturn(report);
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Данные отчета записаны");
    }

    @Test
    void processUpdateReportSaveWithException() throws Exception {
        //Given
        Update update = generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportBehaviorRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenThrow(new InvalidDataException("error"));
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Возникла ошибка: error");
    }

    //Тесты сообщений для/от волонтера
    @Test
    void processUpdateSendQuestionToVolunteer() throws Exception {
        //Given
        Update update = generateUpdate("question");
        Question question = new Question(update.message().chat().id(), 12L);
        //When
        when(questionsBuffer.getQuestionByUserChat(update.message().chat().id())).thenReturn(Optional.of(question));
        out.processUpdates(Collections.singletonList(update));
        //Then
        List<SendMessage> actual = getActualSendMessages();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(12L);
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("1: Сообщение от пользователя, для ответа используйте reply:\n question");
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(11L);
        Assertions.assertThat(actual.get(1).getParameters().get("text")).isEqualTo("Сообщение отправлено волонтеру");
    }

    @Test
    void processUpdateSendReplyFromVolunteer() throws Exception {
        //Given
        String replyMessage = "1: Сообщение от пользователя: вопрос";
        Update update = generateUpdateWithReply(replyMessage, "ответ");
        Question question = new Question(11L, 12L);
        question.setId(1);
        //When
        when(questionsBuffer.getQuestionById(any())).thenReturn(Optional.of(question));
        out.processUpdates(Collections.singletonList(update));
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Ответ волонтера: \n" + "ответ");
    }

    private Update generateUpdate(String text) throws IOException {
        //Так почему-то не работает
        //String json = Files.readString(Path.of(Objects.requireNonNull(KeyboardServiceExtTest.class.getResource("update.json")).toURI()));
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "update.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%text%", text), Update.class);
    }

    private Update generateUpdateWithCallback(String callbackData) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "updateWithCallback.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%data%", callbackData), Update.class);
    }

    private Update generateUpdateWithReply(String replyText, String text) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "updateWithReplyMessage.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));
        json = json.replace("%replyText%", replyText).replace("%text%", text);
        return BotUtils.fromJson(json, Update.class);
    }

    private SendResponse generateResponseOk() {
        return BotUtils.fromJson("""
                { "ok": true }""", SendResponse.class);
    }

    private SendMessage getActualSendMessage() {
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    private List<SendMessage> getActualSendMessages() {
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        return argumentCaptor.getAllValues();
    }
}