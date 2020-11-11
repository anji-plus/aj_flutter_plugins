package hardware.print;

import java.util.List;

/**
 * @author hailong .
 * 单张打印，逐行打印，一行是集合的一组数据，一组数据有多个元素
 * Create on 2019/10/15
 */
public class PrintDto {

    private List<List<KeyBean>> printer;

    public List<List<KeyBean>> getPrinter() {
        return printer;
    }

    public void setPrinter(List<List<KeyBean>> printer) {
        this.printer = printer;
    }

    public static class KeyBean {
//                 "marginLeft": 10, //x轴位置
//                 "marginRight": 10,
//                 "gravity" : 1 // 居1左、2、居中 3、居右
//                 "barWidth": 100, //条形码或二维码宽度
//                 "barHeight": 100, //条形码或二维码高度
//                 "bold": true, //是否粗体
//                 "underLine":true, //是否下划线
//                 "fontSize": 14, //字体
//                 "title": "LSAFASFASF",//标题
//                 "byteMapPath": "XXXX", //图片路径，默认空
//                 "mode": 1 //1、普通的text 2、条形码 3、二维码 4、图片

        private int marginLeft;
        private int marginRight;
        private int gravity;
        private int barWidth;
        private int barHeight;
        private boolean bold;
        private boolean underLine;
        private int fontSize;
        private String title;
        private String byteMapPath;
        private int mode;

        public int getMarginLeft() {
            return marginLeft;
        }

        public void setMarginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
        }

        public int getMarginRight() {
            return marginRight;
        }

        public void setMarginRight(int marginRight) {
            this.marginRight = marginRight;
        }

        public int getGravity() {
            return gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public int getBarWidth() {
            return barWidth;
        }

        public void setBarWidth(int barWidth) {
            this.barWidth = barWidth;
        }

        public int getBarHeight() {
            return barHeight;
        }

        public void setBarHeight(int barHeight) {
            this.barHeight = barHeight;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public boolean isUnderLine() {
            return underLine;
        }

        public void setUnderLine(boolean underLine) {
            this.underLine = underLine;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getByteMapPath() {
            return byteMapPath;
        }

        public void setByteMapPath(String byteMapPath) {
            this.byteMapPath = byteMapPath;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }
    }
}
