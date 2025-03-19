/**
 * 
 */

document.addEventListener('DOMContentLoaded', () => {
	const inputUsername = document.getElementById("username");
	const inputNickname = document.getElementById("nickname");
	const inputEmail = document.getElementById("email");
	const submitBtn = document.getElementById("submitBtn");
	const password = document.getElementById("password");
	const passwordConfirm = document.getElementById("passwordConfirm");
	password.addEventListener("input", validatePasswords);
	passwordConfirm.addEventListener("input", validatePasswords);

	let isUsernameValid = false;
	let isNicknameValid = false;
	let isEmailValid = false;

	inputUsername.addEventListener("input", function() {
		validateUsername();  // 값이 입력될 때마다 유효성 검사 실행
	});

	inputNickname.addEventListener("input", function() {
		validateNickname();  // 값이 입력될 때마다 유효성 검사 실행
	});

	inputEmail.addEventListener("input", function() {
		validateEmail();  // 값이 입력될 때마다 유효성 검사 실행
	});

	/* ---------------------------------------함수--------------------------------------------------- */

	function validatePasswords() {
		if (password.value === passwordConfirm.value) {
			passwordMismatch.style.display = "none"; // 일치하지 않으면 경고 메시지 숨기기
			passwordMatch.style.display = "block"; // 일치하면 "비밀번호가 일치합니다." 메시지 표시
			isValidPassword = true; // 전송 버튼 활성화
		} else {
			passwordMatch.style.display = "none"; // 일치하면 "비밀번호가 일치합니다." 메시지 숨기기
			passwordMismatch.style.display = "block"; // 일치하지 않으면 "비밀번호가 일치하지 않습니다." 메시지 표시
			isValidPassword = false; // 전송 버튼 비활성화
		}
	}


	async function validateUsername() {
		const username = inputUsername.value.trim();

		try {
			const response = await axios.post("/member/checkUsername", { username: username });
			const messageElement = document.getElementById("usernameMessage");
			if (response.data) {
				messageElement.textContent = "사용 불가능한 아이디입니다.";
				messageElement.style.color = "red";
				isUsernameValid = false;
			} else {
				messageElement.textContent = "사용 가능한 아이디입니다.";
				messageElement.style.color = "green";
				isUsernameValid = true;
			}

			updateSubmitButtonState();
		} catch (error) {
			console.error("중복 확인 중 오류 발생:", error);
			isUsernameValid = false;
			updateSubmitButtonState();
		}
	}

	async function validateNickname() {
		const nickname = inputNickname.value.trim();

		try {
			const response = await axios.post("/member/checkNickname", { nickname });
			const messageElement = document.getElementById("nicknameMessage");

			if (response.data) {
				messageElement.textContent = "사용 불가능한 닉네임입니다.";
				messageElement.style.color = "red";
				isNicknameValid = false;
			} else {
				messageElement.textContent = "사용 가능한 닉네임입니다.";
				messageElement.style.color = "green";
				isNicknameValid = true;
			}

			updateSubmitButtonState();
		} catch (error) {
			console.error("중복 확인 중 오류 발생:", error);
			isNicknameValid = false;
			updateSubmitButtonState();
		}
	}

	async function validateEmail() {
		const email = inputEmail.value.trim();

		try {
			const response = await axios.post("/member/checkEmail", { email });
			const messageElement = document.getElementById("emailMessage");

			if (response.data) {
				messageElement.textContent = "사용 불가능한 이메일입니다.";
				messageElement.style.color = "red";
				isEmailValid = false;
			} else {
				messageElement.textContent = "사용 가능한 이메일입니다.";
				messageElement.style.color = "green";
				isEmailValid = true;
			}

			updateSubmitButtonState();
		} catch (error) {
			console.error("중복 확인 중 오류 발생:", error);
			isEmailValid = false;
			updateSubmitButtonState();
		}
	}

	function updateSubmitButtonState() {
		// 모든 필드가 유효한 경우에만 제출 버튼 활성화
		if (isUsernameValid && isNicknameValid && isEmailValid && isValidPassword) {
			submitBtn.disabled = false;
		} else {
			submitBtn.disabled = true;
		}
	}


});