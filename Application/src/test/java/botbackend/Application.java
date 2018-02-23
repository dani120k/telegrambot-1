package botbackend;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.util.ArrayList;


public class Application {

    public static void main(String[] args) throws Exception {

        RSSNewsRequest tmp1 = new RSSNewsRequest();
        ArrayList<News> allNews = tmp1.getRSSNews(RSSNames.ZDOROVIE, 100, 5);
        System.out.println("CountNews = " + allNews.size());
        for(int i = 0; i < allNews.size(); i++) {
            allNews.get(i).writeNews("Test" + (i + 1), "");
        }


        /*VKNewsRequest tmp = new VKNewsRequest();

        ArrayList<News> allNews = tmp.getVKNews(VKNames.CULTURA, 100, 40);

        System.out.println("CountNews = " + allNews.size());
        for(int i = 0; i < allNews.size(); i++){
            allNews.get(i).writeNews("Test"+ (i+1), "");
        }*/


        //VKNewsRequest tmp = new VKNewsRequest();

        //ArrayList<News> allNews = tmp.getVKNews(VKNames., 100, 19);
        //System.out.println(allNews.size() + "size");
        //for(int i = 0; i < allNews.size(); i++){
         //   allNews.get(i).writeNews("4CH"+ (i+1), "");
        //}

        /*ApiContextInitializer.init();
        DataBase.Conn();
        DataBase.CreateDB();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        Bot bot = new Bot();
        try {
            botsApi.registerBot(bot);
            //bot.UpdateNews(allNews);


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/
    }
}
