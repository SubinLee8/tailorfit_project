/**
 * /activities/details.html 에 연결
 */

document.addEventListener('DOMContentLoaded', () => {
	const btnDelete = document.querySelector("#btnDelete");
	btnDelete.addEventListener('click', deleteActivity);

	//댓글 더보기 버튼에서 이용
	let currentPageNo = 0;
	let totalPageNo = 0;
	const recordId = document.querySelector('#id').innerText;
	
	const btnMore = document.querySelector('button#btnMore');
	    btnMore.addEventListener('click', () => getAllComments(currentPageNo + 1));

	getAllComments();
	const btnCreate=document.querySelector("#btnCreate");
	btnCreate.addEventListener('click',registerComment);

	/* --------------------------------------------콜백함수----------------------------------------------- */

	function deleteActivity(event) {
		const response = confirm(`정말 활동을 삭제하시겠습니까?`);
		if (response) {
			location.href = `/activities/delete?id=${recordId}`
		}
	}
	
	
	

	async function getAllComments(pageNo = 0) {
		//디폴트: 페이지 번호 0
		console.log('id='+recordId);
		const url = `/api/comment/all/${recordId}?p=${pageNo}`;
		try {
			//비동기 함수 호출
			const { data } = await axios.get(url); //응답이 올 때까지 기다린다. 
			console.log(data);
			currentPageNo = data.page.number;
			totalPageNo = data.page.totalPages;
			if (currentPageNo == totalPageNo - 1 || data.page.totalElements == 0) {
				btnMore.classList.add('d-none');
			}
			makeCommentElements(data);
		} catch (error) {
			//에러
			console.log(error);
		}

	}

	function makeCommentElements({ content, page }) {
		//로그인 사용자아이디 (네비게이션바에서 찾는다)
		const authUser = document.querySelector('#username').innerText;
		console.log(authUser);

		//댓글 목록을 추가할 div요소
		const divComments = document.querySelector('div#divComments');

		//div에 삽입할 html 문자열
		let htmlStr = '';
		for (const comment of content) {
			htmlStr += `
			    <div class="mt-3 card shadow-sm border-0" style="border-radius: 15px; background-color: #f8f9fa;">
			        <div class="card-body">
			            <div class="d-flex justify-content-between align-items-center">
			                <span class="fw-bold text-primary">👤 ${comment.nickname}</span>
			                <span class="text-secondary small">🕒 ${comment.modifiedTime}</span>
			            </div>
			            <div class="mt-2">
			                <textarea class="commentText form-control" data-id="${comment.id}" 
			                          style="border-radius: 10px; background-color: #ffffff;">${comment.content}</textarea>
			            </div>`;

			if (authUser == comment.username) {
			    htmlStr += `
			        <div class="mt-3 d-flex justify-content-end">
			            <button class="btnDeleteComment btn btn-outline-danger me-2 " 
			                    data-id="${comment.id}" style="border-radius: 10px;">🗑 삭제</button>
			            <button class="btnUpdateComment btn btn-outline-primary " 
			                    data-id="${comment.id}" style="border-radius: 10px;">✏ 수정</button>
			        </div>`;
			}

			htmlStr += `
			        </div>
			    </div>`;
		}
		if (page.number == 0) {
			//페이지가 0인 댓글목록을 가져왔을 때
			divComments.innerHTML = htmlStr;
		} else {
			//현재 페이지가 0이 아닌 댓글목록을 가져왔을 때
			divComments.innerHTML += htmlStr;
		}

		// 댓글 [삭제], [수정] 버튼을 찾고, 클릭 이벤트 리스너를 설정.
		const btnDeletes = document.querySelectorAll('button.btnDeleteComment');
		/*
		for (const btn of btnDeletes) {
			btn.addEventListener('click', deleteComment);
		}
		*/
		btnDeletes.forEach((btn) => btn.addEventListener('click', deleteComment));

		const btnUpdates = document.querySelectorAll('button.btnUpdateComment');
		btnUpdates.forEach((btn) => btn.addEventListener('click', updateComment));
	}

	async function deleteComment(event) {
		//        console.log(event.target);
		const check = confirm('정말 삭제할까요?');
		if (!check) {
			return;
		}

		const id = event.target.getAttribute('data-id');
		const uri = `/api/comment/${id}`;
		try {
			const response = await axios.delete(uri);
			console.log(`deleted comment id = ${response.data}`);
			alert('댓글이 삭제됐습니다.');
			getAllComments(0);
		} catch (error) {
			console.log(error);
		}

	}


	async function updateComment(event) {
		const id = event.target.getAttribute('data-id');
		const text = document.querySelector(`textarea.commentText[data-id="${id}"]`);
		const content = text.value;

		if (content.trim() === '') {
			alert('댓글 내용은 반드시 입력해야 합니다.');
			return;
		}

		const check = confirm('수정된 내용으로 변경할까요?');
		if (!check) {
			return;
		}

		try {
			const response = await axios.put(`/api/comment/${id}`, { id, content });
			console.log(response);
			alert('댓글이 수정되었습니다.');
			getAllComments(0);
		}
		catch (error) {
			console.log(error);
		}
	}



	// 댓글 등록 함수
	async function registerComment() {
		// 댓글 내용
		const content = document.querySelector('textarea#content').value;
		// 댓글 작성자
		const username = document.querySelector('#username').innerHTML;

		if (content.trim() === '') {
			alert('댓글 내용은 반드시 입력해야 합니다.');
			return;
		}

		// Ajax 요청에서 Request Body에 포함시켜서 전송할 데이터
		const reqBody = { recordId, content, username };

		// Ajax 요청을 보내고, 응답/에러 처리
		try {
			const { data } = await axios.post('/api/comment', reqBody);
			console.log(data);

			// 댓글 입력 textarea의 내용을 지움.
			document.querySelector('textarea#content').value = '';

			// 댓글 목록을 다시 그림.
			getAllComments(0);

		} catch (error) {
			console.log(error);
		}

	}



});