package spasko.lptStepper;

import java.util.concurrent.TimeUnit;

import jnpout32.pPort;

/**
 * Created with IntelliJ IDEA. User: spasko Date: 16.09.14 Time: 15:55 To change
 * this template use File | Settings | File Templates.
 */
class Dvijenie {

	volatile int pozitsia_x = 0;
	volatile int pozitsia_y = 0;
	volatile int pozitsia_z = 0;
	int popravka_x = 40000;
	int popravka_y = 40000;
	int popravka_z = 40000;
	int shustvitelnostVumiruvanna;
	int scorostPerehoda = 1000;
	int vverhVnuzY[] = new int[] { 1, 4, 2, 8 };
	private int vlevoVpravoX[] = new int[] { 128, 32, 64, 16 };
	private int vverhVnuzZ[] = { 15, 14, 10, 8, 9, 1, 3, 7 };

	private pPort lptWork = new pPort();

	private int hagovVOtsshetePopravkaX = 2;
	private int hagovVOtsshetePopravkaY = 2;
	private int hagovVOtsshetePopravkaZ = 2;

	private boolean izmenenieNapravleniaDvigeniaX = false;
	private boolean izmenenieNapravleniaDvigeniaY = false;
	private boolean izmenenieNapravleniaDvigeniaZ = false;

	private int pressedBite = 0;

	public Dvijenie() {
		pressedBite = Integer.highestOneBit(lptWork.input((short) (lptWork.portAddress + 1)));
	}

	// Move to one logical step (can be many motor steps)
	public void dvijenieNaOdinHag(Napravlenie_dvigenia napravlenie, boolean izmenenieKoordinat, int scorost) {
		switch (napravlenie) {
		case VVERH:
			popravkaLuftov(Napravlenie_dvigenia.VVERH, scorost);
			if (izmenenieKoordinat)
				pozitsia_y--;
			else
				popravka_y--;
			podashaSignalaXY(scorost);
			break;
		case VNUZ:
			popravkaLuftov(Napravlenie_dvigenia.VNUZ, scorost);
			if (izmenenieKoordinat)
				pozitsia_y++;
			else
				popravka_y++;
			podashaSignalaXY(scorost);
			break;
		case VLEVO:
			popravkaLuftov(Napravlenie_dvigenia.VLEVO, scorost);
			if (izmenenieKoordinat)
				pozitsia_x--;
			else
				popravka_x--;
			podashaSignalaXY(scorost);
			break;
		case VPRAVO:
			popravkaLuftov(Napravlenie_dvigenia.VPRAVO, scorost);
			if (izmenenieKoordinat)
				pozitsia_x++;
			else
				popravka_x++;
			podashaSignalaXY(scorost);
			break;
		case VVERH_VLEVO:
			popravkaLuftov(Napravlenie_dvigenia.VVERH, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VLEVO, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y--;
				pozitsia_x--;
			} else {
				popravka_y--;
				popravka_x--;
			}
			podashaSignalaXY(scorost);
			break;
		case VVERH_VPRAVO:
			popravkaLuftov(Napravlenie_dvigenia.VVERH, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VPRAVO, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y--;
				pozitsia_x++;
			} else {
				popravka_y--;
				popravka_x++;
			}
			podashaSignalaXY(scorost);
			break;
		case VNUZ_VLEVO:
			popravkaLuftov(Napravlenie_dvigenia.VLEVO, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VNUZ, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y++;
				pozitsia_x--;
			} else {
				popravka_y++;
				popravka_x--;
			}
			podashaSignalaXY(scorost);
			break;
		case VNUZ_VPRAVO:
			popravkaLuftov(Napravlenie_dvigenia.VNUZ, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VPRAVO, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y++;
				pozitsia_x++;
			} else {
				popravka_y++;
				popravka_x++;
			}
			podashaSignalaXY(scorost);
			break;
		case PODNAT_INSRUMENT:
			popravkaLuftov(Napravlenie_dvigenia.PODNAT_INSRUMENT, scorost);
			if (izmenenieKoordinat)
				pozitsia_z--;
			else
				popravka_z--;
			podashaSignalaZ(scorost);
			for (int i = 0; i < 4; i++) {
				popravka_z--;
				podashaSignalaZ(scorost);
			}
			break;
		case OPUSTIT_INSRUMENT:
			popravkaLuftov(Napravlenie_dvigenia.OPUSTIT_INSRUMENT, scorost);

			if (izmenenieKoordinat)
				pozitsia_z++;
			else
				popravka_z++;
			podashaSignalaZ(scorost);

			for (int i = 0; i < 4; i++) {
				popravka_z++;
				podashaSignalaZ(scorost);
			}
			break;

		}
	}

	private void popravkaLuftov(Napravlenie_dvigenia napravnenie, int scorost) {
		switch (napravnenie) {
		case VNUZ:
			if (izmenenieNapravleniaDvigeniaY)
				for (int i = 0; i < hagovVOtsshetePopravkaY; i++) {
					popravka_y++;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaY = false;
			break;
		case VVERH:
			if (!izmenenieNapravleniaDvigeniaY)
				for (int i = 0; i < hagovVOtsshetePopravkaY; i++) {
					popravka_y--;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaY = true;
			break;
		case VLEVO:
			if (!izmenenieNapravleniaDvigeniaX)
				for (int i = 0; i < hagovVOtsshetePopravkaX; i++) {
					popravka_x--;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaX = true;
			break;
		case VPRAVO:
			if (izmenenieNapravleniaDvigeniaX)
				for (int i = 0; i < hagovVOtsshetePopravkaX; i++) {
					popravka_x++;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaX = false;
			break;
		case PODNAT_INSRUMENT:
			if (!izmenenieNapravleniaDvigeniaZ)
				for (int i = 0; i < hagovVOtsshetePopravkaZ; i++) {
					popravka_z--;
					podashaSignalaZ(scorost);
				}
			izmenenieNapravleniaDvigeniaZ = true;
			break;
		case OPUSTIT_INSRUMENT:
			if (izmenenieNapravleniaDvigeniaZ)
				for (int i = 0; i < hagovVOtsshetePopravkaZ; i++) {
					popravka_z++;
					podashaSignalaZ(scorost);
				}
			izmenenieNapravleniaDvigeniaZ = false;
			break;
		default:
			throw new IllegalArgumentException("Not valid param \'napravlenie\'");
		}
	}

	// Set signal to two step motors in same moment (XY 2-9 pins LPT)
	private void podashaSignalaXY(int scorost) {
		try {
			TimeUnit.NANOSECONDS.sleep(scorost * 10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lptWork.output(lptWork.portAddress, (byte) (vverhVnuzY[(pozitsia_y + popravka_y) % vverhVnuzY.length]
				+ vlevoVpravoX[(pozitsia_x + popravka_x) % vlevoVpravoX.length]));

	}

	// Set signal to one step motors in same moment (Z 1,14,16,17 pins LPT)
	private void podashaSignalaZ(int scorost) {
		try {
			TimeUnit.NANOSECONDS.sleep(scorost * 5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lptWork.output((short) (lptWork.portAddress + 2),
				(byte) (vverhVnuzZ[(pozitsia_z + popravka_z) % vverhVnuzZ.length]));

	}

	// after work don't use energy to hold motors in same position
	void notSignalXYZ(boolean notX, boolean notY, boolean notZ) {
		lptWork.output(lptWork.portAddress,
				(byte) ((notX ? 0 : vverhVnuzY[(pozitsia_y + popravka_y) % vverhVnuzY.length])
						+ (notY ? 0 : vlevoVpravoX[(pozitsia_x + popravka_x) % vlevoVpravoX.length])));
		lptWork.output((short) (lptWork.portAddress + 2),
				(byte) (notZ ? 11 : vverhVnuzZ[(pozitsia_z + popravka_z) % vverhVnuzZ.length]));
	}

	// Going to coordinates in some logic (in first phase moving Z, in second
	// XY)
	public void goTo(int x, int y, int z, int scorost) {
		int xx = pozitsia_x;
		int yy = pozitsia_y;
		int zz = pozitsia_z;
		if (xx == x && yy == y && zz == z)
			return;
		if (z - zz > 0)
			for (int i = 1; i <= z - zz; i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.OPUSTIT_INSRUMENT, true, scorost);
			}
		else
			for (int i = 1; i <= zz - z; i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.PODNAT_INSRUMENT, true, scorost);
			}
		if (y - yy > 0 && x - xx > 0)
			for (int i = 1; i <= Math.min(y - yy, x - xx); i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VNUZ_VPRAVO, true, scorost);
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy > 0 && x - xx < 0)
			for (int i = 1; i <= Math.min(y - yy, xx - x); i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VNUZ_VLEVO, true, scorost);
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy < 0 && x - xx > 0)
			for (int i = 1; i <= Math.min(yy - y, x - xx); i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VVERH_VPRAVO, true, scorost);
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy < 0 && x - xx < 0)
			for (int i = 1; i <= Math.min(yy - y, xx - x); i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VVERH_VLEVO, true, scorost);
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy > 0)
			for (int i = 1; i <= y - yy; i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VNUZ, true, scorost);
			}
		else
			for (int i = 1; i <= yy - y; i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VVERH, true, scorost);
			}
		if (x - xx > 0)
			for (int i = 1; i <= x - xx; i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VPRAVO, true, scorost);
			}
		else
			for (int i = 1; i <= xx - x; i++) {
				dvijenieNaOdinHag(Napravlenie_dvigenia.VLEVO, true, scorost);
			}
	}

	// read data from LPT detector, on start program we think it wasn't pressed
	public boolean nowKontact() {
		return pressedBite != Integer.highestOneBit(lptWork.input((short) (lptWork.portAddress + 1)));
	}

}
