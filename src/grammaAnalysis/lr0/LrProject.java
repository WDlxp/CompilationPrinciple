package grammaAnalysis.lr0;

/**
 * LR0的每个项目
 */
class LrProject {
    /**
     * LR0项目的左边
     */
    char leftSide;
    /**
     * LR0项目的右边
     */
    String rightSide;
    /**
     * LR0项目的原点
     */
    int dotPointer;

    public LrProject(char leftSide, String rightSide, int dotPointer) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.dotPointer = dotPointer;
    }

    public LrProject(char leftSide, String rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.dotPointer = 0;
    }

    public void dotPointerPlus() {
        this.dotPointer++;
    }

    /**
     * 比较两个项目是否相等
     * @param lrProject 项目
     * @return 返回是否相等
     */
    public boolean isEqual(LrProject lrProject) {
        return (this.leftSide == lrProject.leftSide) && (this.rightSide.equals(lrProject.rightSide)) && (this.dotPointer == lrProject.dotPointer);
    }

    /**
     * 判断是否已经是规约项目
     */
    public boolean isDotLast() {
        //右侧为空的情况考虑
        if (rightSide.equals("")) {
            return true;
        }
        return dotPointer == rightSide.length();
    }

    /**
     * 复制一个项目
     */
    public LrProject lrProjectClone() {
        return new LrProject(leftSide, rightSide, dotPointer);
    }
}