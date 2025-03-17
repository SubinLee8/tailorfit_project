package com.itwill.tailorfit.domain;

public enum MemberRole {
	ATHLETE("ROLE_ATHLETE"), TRAINER("ROLE_TRAINER"), GUEST("ROLE_GUEST");

	private String authority;

	MemberRole(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}
}
