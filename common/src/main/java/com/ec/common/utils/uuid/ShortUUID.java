package com.ec.common.utils.uuid;

/**
 *
 */
public class ShortUUID {
    public static String nextID() {
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        StringBuffer shortBuffer = new StringBuffer();
        // 生成一个随机的UUID并去除其中的"-"符号
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 循环8次 每次只取UUID的一个子串 然后将其转换为十六进制
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            // 使用取模运算将十六进制映射到字符数组中对应的字符
            shortBuffer.append(chars[x % 0x3E]);
        }
        String suffix = shortBuffer.toString();

        return suffix;
    }
}
