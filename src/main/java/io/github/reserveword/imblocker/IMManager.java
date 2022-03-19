package io.github.reserveword.imblocker;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class IMManager {

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

    static {
        Native.register("imm32");
    }

    private static final User32 u = User32.INSTANCE;

    private static boolean state = true;

    private static void makeOnImp() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    private static void makeOffImp() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc != null) {
            ImmDestroyContext(himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    private static boolean toggleImp() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
            ImmReleaseContext(hwnd, himc);
            return true;
        } else {
            himc = ImmAssociateContext(hwnd, null);
            ImmDestroyContext(himc);
            ImmReleaseContext(hwnd, himc);
            return false;
        }
    }

    public static void makeOn() {
        if (!state) {
            state = true;
            makeOnImp();
        }
    }

    public static void makeOff() {
        if (state) {
            state = false;
            makeOffImp();
        }
    }

    public static boolean getState() {
        return state;
    }

    public static boolean toggle() {
        state = !state;
        return toggleImp();
    }
}
