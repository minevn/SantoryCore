package mk.plugin.santory.skill;

import mk.plugin.santory.skills.*;

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
	DANH_THUONG("Đánh thường", new WSHeavyHit(), 5),
	XOAY_KY("Xoáy kỹ", new SkillXoayKy(), 0),
	SET_DIEN("Sét điện", new SkillSetDien(), 0),
	TU_THAN("Tử thần", new SkillTuThan(), 5),
	HUY_DIET("Hủy diệt", new SkillHuyDiet(), 5),
	HAC_SINH("Hắc sinh", new SkillHacSinh(), 5),
	DOC_TO("Độc tố", new SkillDocTo(), 5),
	DAI_MA("Đại ma", new SkillDaiMa(), 5),
	BAO_TEN("Bão tên", new SkillBaoTen(), 5),
	TRUONG_TRONG_LUC("Trường trọng lực", new SkillTruongTrongLuc(), 10),
	BOC_PHA_THIEN("Bộc phá thiên", new SkillBocPhaThien(), 12);

	;
	
	private final String name;
	private final SkillExecutor executor;
	private final int cooldown;
	
	Skill(String name, SkillExecutor executor, int cooldown) {
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
