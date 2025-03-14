/**
 * /activities/details.html 에 연결
 */

document.addEventListener('DOMContentLoaded', () => {
	const btnDelete = document.querySelector("#btnDelete");
	btnDelete.addEventListener('click', deleteActivity);

	//댓글 더보기 버튼에서 이용
	let currentPageNo = 0;
	let totalPageNo = 0;
	
	getAllComments();

	/* --------------------------------------------콜백함수----------------------------------------------- */

	function deleteActivity(event) {
		const id = document.querySelector("#id").innerHTML;
		console.log(id);
		const response = confirm(`정말 활동을 삭제하시겠습니까?`);
		if (response) {
			location.href = `/activities/delete?id=${id}`
		}
	}

	async function getAllComments(pageNo = 0) {
		//디폴트: 페이지 번호 0
		const postId = document.querySelector('input#id').value;
		const url = `/api/comment/all/${postId}?p=${pageNo}`;
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


});