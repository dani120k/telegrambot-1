package botbackend;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.Link;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class VKNewsRequest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private Properties properties;
    private HttpTransportClient transportClient;
    private VkApiClient vk;
    private ServiceActor serviceActor;
    private Integer clientid;
    private String servisetoken;
    private HashMap<VKNames, Long> lastTime;


    public VKNewsRequest(){
        loadConfiguration();
        clientid = Integer.valueOf(properties.getProperty("client.id"));
        servisetoken = properties.getProperty("servise.token");
        transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);
        serviceActor = new ServiceActor(clientid, servisetoken);
        Long currentTime = System.currentTimeMillis() / 1000L;
        lastTime = new HashMap<>();
        for(VKNames vkNames : VKNames.values()){
            lastTime.put(vkNames, currentTime - 86400); //делаем последнее время = день
        }
    }

    public ArrayList<News> getVKNews(VKNames vkNames, int percentzip, int maxcount) throws Exception{
        ArrayList<News> result = new ArrayList<>();

        List<WallPostFull> list;
        list = vk.wall().get(serviceActor).
                ownerId(-vkNames.ID()).
                count(maxcount).
                filter(WallGetFilter.OWNER).
                execute().getItems();
        for(WallPostFull post : list){
            if(post.getDate() > lastTime.get(vkNames)){
                String text;
                Integer time;
                String links = "";
                ArrayList<BufferedImage> vkImages = null;
                BufferedImage image = null;
                text = post.getText();
                time = post.getDate(); // Они держат время поста в int и лет через 15 он переполниться у них
                List<WallpostAttachment> wallpostAttachments = post.getAttachments();

                for(WallpostAttachment wallpostAttachment : wallpostAttachments){
                    Link link = wallpostAttachment.getLink();
                    if(link != null){
                        links += link.getUrl() + "\n";
                    }

                    Photo photo = wallpostAttachment.getPhoto();
                    if(photo != null){
                        if(vkImages == null){
                            vkImages = new ArrayList<>();
                            vkImages.add(takeBestPicture(photo));
                        }
                        else {
                            vkImages.add(takeBestPicture(photo));
                        }
                    }
                }
                if(vkImages != null){
                    image = MethodsNews.createBigPicture(vkImages);
                }
                result.add(new News(text, image, links, time));
            }
        }
        return result;
    }

    private BufferedImage takeBestPicture(Photo photo) throws Exception{
        BufferedImage image;
        URL url;
        if(photo.getPhoto2560() != null){
            url = new URL(photo.getPhoto2560());
        }
        else if(photo.getPhoto1280() != null){
            url = new URL(photo.getPhoto1280());
        }
        else if(photo.getPhoto807() != null){
            url = new URL(photo.getPhoto807());
        }
        else if(photo.getPhoto604() != null){
            url = new URL(photo.getPhoto604());
        }
        else if(photo.getPhoto130() != null){
            url = new URL(photo.getPhoto130());
        }
        else if(photo.getPhoto75() != null){
            url = new URL(photo.getPhoto75());
        }
        else{
            throw new IOException("Can't find picture");
        }
        image = ImageIO.read(url);
        return image;
    }

    private void loadConfiguration() {
        properties = new Properties();
        try (InputStream is = Application.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("Can't load properties file", e);
            throw new IllegalStateException(e);
        }
    }
}
