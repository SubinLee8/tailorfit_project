/**
 * activities/mydashboard.html
 */

document.addEventListener('DOMContentLoaded', () => {
	//문자열이 들어가므로 파싱해주기
	const runningRoutine = JSON.parse(document.getElementById('runningRoutine').value);
	const walkingRoutine = JSON.parse(document.getElementById('walkingRoutine').value);
	const stretchingRoutine = JSON.parse(document.getElementById('stretchingRoutine').value);
	const weights = JSON.parse(document.getElementById('weights').value);
	const runningTimes = JSON.parse(document.getElementById('runningTimes').value);
	const walkingsTimes = JSON.parse(document.getElementById('walkingsTimes').value);
	const stretchingsTimes = JSON.parse(document.getElementById('stretchingsTimes').value);

	const totalWalking = walkingsTimes.reduce((sum, value) => sum + value, 0);
	const totalRunning = runningTimes.reduce((sum, value) => sum + value, 0);
	const totalStretching = stretchingsTimes.reduce((sum, value) => sum + value, 0);

	// 전체 합계 구하기
	const total = totalWalking + totalRunning + totalStretching;

	// 각 운동의 비율 계산
	const walkingRatio = (totalWalking / total) * 100;
	const runningRatio = (totalRunning / total) * 100;
	const stretchingRatio = (totalStretching / total) * 100;

	const review = document.getElementById('review');
	getReview();
	console.log(runningRoutine+'권장달리기');

	/*-------------------------------------콜백함수----------------------------------------------------------------- */

	function getReview() {
		let runReview = '';
		let walkReview = '';
		let stretchReview = '';
		if (totalRunning / 4 >= runningRoutine) {
			runReview = `권장 달리기 시간인 ${runningRoutine}(분)보다 많이 달리고 있습니다`;
		} else {
			runReview = `권장 달리기 시간인 ${runningRoutine}(분)보다 적게 달리고 있습니다`;
		}
		if (totalWalking / 4 >= walkingRoutine) {
			walkReview = `권장 걷기 시간인 ${walkingRoutine}(분)보다 많이 걷고 있습니다`;
		} else {
			walkReview = `권장 걷기 시간인 ${walkingRoutine}(분)보다 적게 걷고 있습니다`;
		}
		if (totalStretching / 4 >= stretchingRoutine) {
			stretchReview = `권장 스트레칭 시간인 ${stretchingRoutine}(분)보다 많이 스트레칭하고 있습니다`;
		} else {
			stretchReview = `권장 스트레칭 시간인 ${stretchingRoutine}(분)보다 적게 스트레칭하고 있습니다`;
		}
		review.innerHTML+=runReview;
		review.innerHTML+=walkReview;
		review.innerHTML+=stretchReview;

	}




	/*--------------------------------------- 그래프 -------------------------------- */

	const ctx = document.getElementById('lineChart').getContext('2d');

	const lineChart = new Chart(ctx, {
		type: 'line',
		data: {
			labels: ['Current', '1 Week Ago', '2 Week Ago', '3 Week Ago'], // 주간 라벨
			datasets: [
				{
					label: '걸은 시간(분)',
					data: walkingsTimes, // 각 주차의 몸무게 변화량
					borderColor: 'rgba(75, 192, 192, 1)', // 라인 색상
					fill: false, // 라인 아래를 채우지 않음
					tension: 0.1
				},
				{
					label: '달린 시간(분)',
					data: runningTimes, // 각 주차의 달린 거리
					borderColor: 'rgba(153, 102, 255, 1)', // 라인 색상
					fill: false,
					tension: 0.1
				},
				{
					label: '스트레칭 시간(분)',
					data: stretchingsTimes, // 각 주차의 달린 거리
					borderColor: 'rgba(255, 99, 132, 1)',
					fill: false,
					tension: 0.1
				}
			]
		},
		options: {
			responsive: true,
			scales: {
				x: {
					title: {
						display: true,
						text: '주차'
					}
				},
				y: {
					title: {
						display: true,
						text: '수치'
					}
				}
			}
		}
	});


	const ctxPie = document.getElementById('pieChart').getContext('2d');

	const pieChart = new Chart(ctxPie, {
		type: 'pie',
		data: {
			labels: ['걷기', '스트레칭', '달리기'], // 운동 종류
			datasets: [{
				data: [walkingRatio, stretchingRatio, runningRatio], // 각 운동에 할애한 시간 비율 (예시)
				backgroundColor: ['rgba(75, 192, 192, 0.2)', 'rgba(153, 102, 255, 0.2)', 'rgba(255, 159, 64, 0.2)'],
				borderColor: ['rgba(75, 192, 192, 1)', 'rgba(153, 102, 255, 1)', 'rgba(255, 159, 64, 1)'],
				borderWidth: 1
			}]
		},
		options: {
			responsive: true
		}
	});


	const ctxBar = document.getElementById('barChart').getContext('2d');

	const barChart = new Chart(ctxBar, {
		type: 'bar',
		data: {
			labels: ['Current', '1 Week Ago', '2 Week Ago', '3 Week Ago'], // 주차
			datasets: [{
				label: '몸무게 변화량 (km)',
				data: weights, // 각 주차의 달린 거리
				backgroundColor: 'rgba(255, 159, 64, 0.2)',
				borderColor: 'rgba(255, 159, 64, 1)',
				borderWidth: 1
			}]
		},
		options: {
			responsive: true,
			scales: {
				x: {
					title: {
						display: true,
						text: '주차'
					}
				},
				y: {
					title: {
						display: true,
						text: '달린 거리 (km)'
					}
				}
			}
		}
	});


});