package managers;

public class Managers {

    public static TaskManager getDefault(){
        return new inMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory (){
        return  new inMemoryHistoryManager();
    }



}
