package grammaAnalysis.lr0;

import java.util.ArrayList;

/**
 * LR0的每一个状态集
 *
 * @author wdl
 */
public class LrState {
    /**状态的核项目*/
    ArrayList<LrProject> coreItems;
    /**核求闭包所得项目*/
    ArrayList<LrProject> closureItems;

    /**状态编号*/
    int stateIndex;

    public LrState() {
        coreItems = new ArrayList<>();
        closureItems = new ArrayList<>();
    }
    public LrState(ArrayList<LrProject> coreItems, ArrayList<LrProject> closureItems, int stateIndex) {
        this.coreItems = coreItems;
        this.closureItems = closureItems;
        this.stateIndex = stateIndex;
    }
    /**
     * 比较核是否相等，也就比较两个状态是否相等
     * @param coreItems 核
     * @return 是否相等
     */
    public boolean isEqual(ArrayList<LrProject> coreItems) {
        //首先通过判断核中项目的个数是否相等如果个数都不相等那么肯定不相等
        if (this.coreItems.size() != coreItems.size()) {
            return false;
        }
        for (int i = 0; i < this.coreItems.size(); i++) {
            if (!this.coreItems.get(i).isEqual(coreItems.get(i))) {
                return false;
            }
        }
        return true;
    }
}
