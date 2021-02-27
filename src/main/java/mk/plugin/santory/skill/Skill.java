package mk.plugin.santory.skill;

import mk.plugin.santory.skills.WSBaoKiem;
import mk.plugin.santory.skills.WSBongDem;
import mk.plugin.santory.skills.WSChiaCat;
import mk.plugin.santory.skills.WSDapRiu;
import mk.plugin.santory.skills.WSDiaChan;
import mk.plugin.santory.skills.WSDienTinh;
import mk.plugin.santory.skills.WSHeavyHit;
import mk.plugin.santory.skills.WSMuaTen;
import mk.plugin.santory.skills.WSQuetKiem;
import mk.plugin.santory.skills.WSTanXaTien;
import mk.plugin.santory.skills.WSThauXuong;
import mk.plugin.santory.skills.WSThienPhat;
import mk.plugin.santory.skills.WSXoayNuoc;
import mk.plugin.santory.skills.WSXungPhong;

public enum Skill {
	
	QUET_KIEM("Quét kiếm", new WSQuetKiem(), 5),	
	XA_TIEN("Xạ tiễn", new WSTanXaTien(), 5),	
	TRONG_RIU("Trọng kiếm", new WSDapRiu(), 5),	
	THAU_XUONG("Thấu xương", new WSThauXuong(), 5),	
	THIEN_PHAT("Thiên phạt", new WSThienPhat(), 5),	
	CHIA_CAT("Chia cắt", new WSChiaCat(), 5),	
	BAO_KIEM("Bão kiếm", new WSBaoKiem(), 5),	
	LUOI_TINH("Lưới tình", new WSDienTinh(), 5),
	XOAY_NUOC("Xoáy nước", new WSXoayNuoc(), 5),
	MAN_DEM("Màn đêm", new WSBongDem(), 5),
	DIA_CHAN("Địa chấn", new WSDiaChan(), 5),
	XUNG_PHONG("Xung phong", new WSXungPhong(), 5),
	MUA_TEN("Mưa tên", new WSMuaTen(), 5),
	DANH_THUONG("Đánh thường", new WSHeavyHit(), 5);
	
	private String name;
	private SkillExecutor executor;
	private int cooldown;
	
	private Skill(String name, SkillExecutor executor, int cooldown) {
		this.name = name;
		this.executor = executor;
		this.cooldown = cooldown;
	}
	
	public SkillExecutor getExecutor() {
		return this.executor;
	}
	
	public int getCooldown() {
		return this.cooldown;
	}
	
	public String getName() {
		return this.name;
	}

}
