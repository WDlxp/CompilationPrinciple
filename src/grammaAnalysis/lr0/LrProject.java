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
     * 辅助判断是否为原点.是否在最后一位
     */
    private int lastIndex;
    /**
     * LR0项目的原点
     */
    int dotPointer;

    public LrProject(char leftSide, String rightSide, int dotPointer) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.dotPointer = dotPointer;
        this.lastIndex = rightSide.length();
    }

    public LrProject(char leftSide, String rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.dotPointer = 0;
    }

    /**
     * 原点后移
     */
    public void dotPointerPlus() {
        this.dotPointer++;
    }

    /**
     * 获取原点.后面的字符
     *
     * @return 原点.后面的字符
     */
    Character dotPointerNext() {
        return rightSide.charAt(dotPointer);
    }
    /**
     * 比较两个项目是否相等
     *
     * @param lrProject 项目
     * @return 返回是否相等
     */
    boolean isEqual(LrProject lrProject) {
        return (this.leftSide == lrProject.leftSide)
                && (this.rightSide.equals(lrProject.rightSide))
                && (this.dotPointer == lrProject.dotPointer);
    }
    /**
     * 判断是否已经是规约项目
     */
    boolean isDotLast() {
        return dotPointer == lastIndex;
    }
    /**
     * 复制一个项目
     */
    LrProject lrProjectClone() {
        return new LrProject(leftSide, rightSide, dotPointer);
    }
}