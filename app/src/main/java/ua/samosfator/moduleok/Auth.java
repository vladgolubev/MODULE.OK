package ua.samosfator.moduleok;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;
import ua.samosfator.moduleok.event.LoginEvent;

public class Auth {
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.45 Safari/537.36";
    private final String INCORRECT_PASSWORD_MESSAGE = "Не вірний логін/пароль.";

    private boolean success;

    private static Student student;

    public void signIn(String login, String password) {
        EventBus.getDefault().register(this);

        Connection.Response loginResponse = null;
        try {
            loginResponse = Jsoup.connect("http://mod.tanet.edu.te.ua/site/login")
                    .userAgent(USER_AGENT)
                    .data("LoginForm[login]", login)
                    .data("LoginForm[password]", password)
                    .data("LoginForm[rememberMe]", "1")
                    .data("yt0", "Увійти")
                    .method(Connection.Method.POST)
                    .execute();
            Document loginDocument = loginResponse.parse();
            success = !(loginDocument.text().contains(INCORRECT_PASSWORD_MESSAGE));
            if (success) {
                Preferences.save("SESSIONID", loginResponse.cookie("PHPSESSID"));
                success = true;
            } else {
                Preferences.save("SESSIONID", "");
                success = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public static Student getCurrentStudent() {
        if (student == null) {
            initStudent();
        }
        return student;
    }

    public void onEvent(LoginEvent event) {
        initStudent();
    }

    private static void initStudent() {
        student = new Student(getMainPageHtml());
    }

    public static void refreshStudent() {
        if (!Auth.isLoggedIn()) return;
        student = new Student(loadMainPage());
    }

    private static String getMainPageHtml() {
        if (!Auth.isLoggedIn()) {
            throw new IllegalArgumentException("MUST LOG IN AT FIRST");
        }
        final String savedMainPageHtml = Preferences.read("mainPageHtml", "");
        String mainPageHtml;

        if (savedMainPageHtml.length() < 1400) {
            mainPageHtml = loadMainPage();
            Preferences.save("mainPageHtml", mainPageHtml);
        } else {
            mainPageHtml = savedMainPageHtml;
        }
        return mainPageHtml;
    }

    private static String loadMainPage() {
        LoadPageAsyncTask loadPageAsyncTask = new LoadPageAsyncTask();
        loadPageAsyncTask.execute();
        try {
            return loadPageAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isLoggedIn() {
        return !Preferences.read("SESSIONID", "").equals("");
    }
}
