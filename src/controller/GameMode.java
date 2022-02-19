package controller;

public enum GameMode {
    AllIntrudersNeedToFinish,
    AnyIntruderNeedToFinish;

    public GameMode getWhichGameMode(int num) {
        if (num == 1) {
            return AllIntrudersNeedToFinish;
        }
        else if (num == 2) {
            return AnyIntruderNeedToFinish;
        }
        return null;
    }
}
