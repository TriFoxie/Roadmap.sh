import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class App {
    public static void main(String[] args) throws Exception {
        //Take input
        Scanner sc = new Scanner(System.in);
        String inpStr = sc.nextLine();
        sc.close();
        String[] input = inpStr.split(" ");

        //Get data as string
        try{
            URL gitUrl = new URL("https://api.github.com/users/" + input[0] + "/events");
            java.net.URLConnection connection = gitUrl.openConnection();
            InputStream is = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            JsonArray events = JsonParser.parseReader(reader).getAsJsonArray();
            reader.close();
            is.close();

            //Actions based on input
            //Input pattern: <github_username> <parameters>
            String ref, refType, type, repo, action, size, release;
            String response = "";
            JsonObject payload;

            for (JsonElement event : events) {
                response += "\n";
                type = event.getAsJsonObject().get("type").getAsString();
                repo = event.getAsJsonObject().get("repo").getAsJsonObject().get("name").getAsString();
                payload = event.getAsJsonObject().get("payload").getAsJsonObject();
                switch (type) {
                    case "CommitCommentEvent": response += "💬 Commented on a commit in"; break;
                    case "CreateEvent":
                        ref = payload.get("ref").getAsString();
                        refType = payload.get("ref_type").getAsString();
                        response += "❇️ Created " + refType + " " + ref + " in";
                        break;
                    case "DeleteEvent":
                        ref = payload.get("ref").getAsString();
                        refType = payload.get("ref_type").getAsString();
                        response += "🚮 Deleted " + refType + " " + ref + " in";
                        break;
                    case "ForkEvent": response += "- Created a fork of"; break;
                    case "GollumEvent": response += "- Updated wiki for"; break;
                    case "IssueCommentEvent":
                        action = payload.get("action").getAsString();
                        response += "💬 " + action + "a comment on an issue in";
                        break;
                    case "IssuesEvent":
                        action = payload.get("action").getAsString();
                        response += "🅾️ " + action + "an issue in";
                        break;
                    case "MemberEvent": response += "🚹 Member event in"; break;
                    case "PublicEvent": response += "📖 Publicised"; break;
                    case "PullRequestEvent":
                        action = payload.get("action").getAsString();
                        response += "⬇️ " + action + " a pull request in";
                        break;
                    case "PullRequestReviewEvent":
                        action = payload.get("action").getAsString();
                        response += "💬 " + action + " a pull request review in";
                        break;
                    case "PullRequestReviewCommentEvent":
                        action = payload.get("action").getAsString();
                        response += "💬 " + action + " a comment on a pull request review in";
                        break;
                    case "PullRequestReviewThreadEvent":
                        action = payload.get("action").getAsString();
                        response += "💬 Marked a pull request comment thread as " + action + " in";
                        break;
                    case "PushEvent":
                        size = payload.get("size").getAsString();
                        response += "⏫ Pushed " + size + " commits to";
                        break;
                    case "ReleaseEvent":
                        release = payload.get("release").getAsJsonObject().get("name").getAsString();
                        response += "📖 Published release " + release + " in";
                        break;
                    case "SponsorshipEvent": response += "👑 Sponsorship created for"; break;
                    case "WatchEvent": response += "⭐ Starred"; break;
                    default:
                        response += "❌ Unsupported event in";
                        break;
                }
                response += " " + repo;
            }
            if ("".equals(response)){
                System.out.println("No activity from this user");
            }else{
                System.out.println(response);
            }
        }
        catch (Exception e){
            String ex = e.toString().split(" ")[0];
            if ("java.io.FileNotFoundException:".equals(ex)){
                System.out.println("[Error] User not found.");
            }else if ("java.io.IOException:".equals(ex.toString())){
                System.out.println("[Error] Rate Limit, add a token to remove this.");
            }else{
                System.out.println("[Error] " + ex);
            }
        }
    }
}

