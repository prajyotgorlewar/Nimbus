package com.example.nimbus; // Make sure this is the correct package

public class AvatarUtils {

    public static int getAvatarResource(int serial) {
        switch (serial) {
            case 1:
                return R.drawable.avatar_1;
            case 2:
                return R.drawable.avatar_2;
            case 3:
                return R.drawable.avatar_3;
            case 4:
                return R.drawable.avatar_4;
            case 5:
                return R.drawable.avatar_5;
            case 6:
                return R.drawable.avatar_6;
            case 7:
                return R.drawable.avatar_7;
            case 8:
                return R.drawable.avatar_8;
            case 9:
                return R.drawable.avatar_9;
            case 10:
                return R.drawable.avatar_10;
            default:
                return R.drawable.avatar_1; // Default avatar
        }
    }
}