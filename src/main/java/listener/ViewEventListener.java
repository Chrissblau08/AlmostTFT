package listener;

public interface ViewEventListener {
    void onBoardCellSelected(int index, String source);
    void onBankAction(int bankItemId, String source);
    void onBankInfoAction(int index, String flag);
}
