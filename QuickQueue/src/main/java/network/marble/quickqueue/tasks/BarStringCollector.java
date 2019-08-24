package network.marble.quickqueue.tasks;

import org.bukkit.Bukkit;

import network.marble.quickqueue.QuickQueue;

public class BarStringCollector implements Runnable{

    @Override
    public void run() {
        getStrings(new BarStringProcessingCallback(){
//            @Override
//            public void onBarStringsProcessed(ArrayList<IdentifiablePluginString> strings){
//                if(strings != null){
//                    BossBarManagerAPI.updateStrings(QuickQueue.getInstance().getName(), strings);
//                }
//            }
        });
    }

    public static void getStrings(final BarStringProcessingCallback callback){//TODO Have a rabbit updating and calling back with main thread task scheduling to do updates
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), new Runnable(){
            @Override
            public void run(){
//                ArrayList<IdentifiablePluginString> strings = new ArrayList<IdentifiablePluginString>();

                /*if(QuickQueue.minigames!=null){
                    try(Object jedis = null){
                        for(Minigame mg : QuickQueue.minigames){
                            String gameID = mg.getId().toString();
                            String key = "gameavgwait:" + gameID;

                            String string = ChatColor.GOLD + "Approximate wait for ";
                            string += ChatColor.GREEN + mg.getGameName();
                            string += ChatColor.GOLD + ": ";
                            if(jedis.exists(key)){
                                String avgTimeString = jedis.get(key);
                                String finalAvgTimeString;

                                double waitSeconds = Integer.valueOf(avgTimeString);
                                if(waitSeconds>0){
                                    double waitMinutes = Math.ceil(waitSeconds/60);
                                    finalAvgTimeString = "\u2248" + (int)waitMinutes + " minute";
                                    if((int)waitMinutes > 1){
                                        finalAvgTimeString += "s";
                                    }
                                    finalAvgTimeString += ".";
                                }else{
                                    finalAvgTimeString = "nearly instant.";
                                }
                                string += finalAvgTimeString;

                            }else{
                                string += "calculating...";
                            }
                            strings.add(new IdentifiablePluginString(gameID, new PluginString(QuickQueue.getInstance().getName(), string)));
                        }
                    }

                }*/




                Bukkit.getScheduler().runTask(QuickQueue.getInstance(), new Runnable(){
                    @Override
                    public void run(){
//                        callback.onBarStringsProcessed(strings);
                    }
                });
            }
        });
    }

}
