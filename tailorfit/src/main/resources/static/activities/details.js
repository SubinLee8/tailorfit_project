/**
 * /activities/details.html 에 연결
 */

document.addEventListener('DOMContentLoaded', () => {
	const btnDelete=document.querySelector("#btnDelete");
	btnDelete.addEventListener('click', deleteActivity);
	
	/* --------------------------------------------콜백함수----------------------------------------------- */
	
	function deleteActivity(event){
		const id=document.querySelector("#id").innerHTML;
		console.log(id);
		const response=confirm(`정말 활동을 삭제하시겠습니까?`);
		if(response){
			location.href=`/activities/delete?id=${id}`
		}
	}
	
});