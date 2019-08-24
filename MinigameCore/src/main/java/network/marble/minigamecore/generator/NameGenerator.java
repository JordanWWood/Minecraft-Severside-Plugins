package network.marble.minigamecore.generator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameGenerator {

    //TODO revamp with own internal generator
    public static String getRandomName(){
        String name;
        String backUpName = "SMASHPANDA";
        try {
            URL nsanameganerator = new URL("http://www.nsanamegenerator.com/");
            BufferedReader in = new BufferedReader(new InputStreamReader(nsanameganerator.openStream(), "UTF-8"));
            String inputLine;
            StringBuilder body = new StringBuilder();
            while ((inputLine = in.readLine()) != null) body.append(inputLine);
            in.close();
            Matcher m = Pattern.compile(">([A-Z]+)</body>").matcher(body.toString());
            if (!m.find() || m.groupCount() <= 1) name = backUpName;
            name = m.group(1);
        }catch(Exception e){
            name = backUpName;
        }
        return name;
    }
}
