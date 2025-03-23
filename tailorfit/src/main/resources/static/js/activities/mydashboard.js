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
	//const createdTimes = JSON.parse(document.getElementById('createdTimes').value);
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

	/*-------------------------------------콜백함수----------------------------------------------------------------- */

	function convertToDaysAgo(createdTimes) {
		console.log(createdTimes);
		if (!Array.isArray(createdTimes)) {
			console.error('createdTimes need to be arrays.');
			return [];
		}

		const now = new Date();

		return createdTimes.map(timeStr => {
			const time = new Date(timeStr);
			const diffTime = now - time; // 밀리초 단위 차이
			const daysAgo = Math.floor(diffTime / (1000 * 60 * 60 * 24)); // 일 단위 변환

			return daysAgo === 0 ? "today" : `${daysAgo} ago`;
		});
	}
	function getReview() {
		let runReview = '';
		let walkReview = '';
		let stretchReview = '';
		if (totalRunning / 4 >= runningRoutine) {
			runReview = `You are running more than the recommended weekly running time of <span class="highlight">${runningRoutine}</span> minutes! <span class="emoji">🏃‍♀️💨</span><br>`;
		} else {
			runReview = `You are running less than the recommended weekly running time of <span class="highlight">${runningRoutine}</span> minutes. <span class="emoji">🏃‍♀️💨</span><br>`;
		}
		if (totalWalking / 4 >= walkingRoutine) {
			walkReview = `You are walking more than the recommended weekly walking time of <span class="highlight">${walkingRoutine}</span> minutes. <span class="emoji">🚶‍♂️💨</span><br>`;
		} else {
			walkReview = `You are walking less than the recommended weekly walking time of <span class="highlight">${walkingRoutine}</span> minutes. <span class="emoji negative">🙁</span><br>`;
		}
		if (totalStretching / 4 >= stretchingRoutine) {
			stretchReview = `You are stretching more than the recommended weekly stretching time of <span class="highlight">${stretchingRoutine}</span> minutes. <span class="emoji">🧘‍♀️💪</span><br>`;
		} else {
			stretchReview = `You are stretching less than the recommended weekly stretching time of <span class="highlight">${stretchingRoutine}</span> minutes. <span class="emoji negative">🙁</span><br>`;
		}

		review.innerHTML += runReview;
		review.innerHTML += walkReview;
		review.innerHTML += stretchReview;

	}




	/*--------------------------------------- 그래프 -------------------------------- */

	const ctx = document.getElementById('lineChart').getContext('2d');

	const lineChart = new Chart(ctx, {
		type: 'line',
		data: {
			labels: ['3 Weeks Ago', '2 Weeks Ago', '1 Week Ago', 'Current'], // 주간 라벨
			datasets: [
				{
					label: 'Toal Walk(min)',
					data: walkingsTimes, // 각 주차의 몸무게 변화량
					borderColor: 'rgba(75, 192, 192, 1)', // 라인 색상
					fill: false, // 라인 아래를 채우지 않음
					tension: 0.1
				},
				{
					label: 'Toal Run(min)',
					data: runningTimes, // 각 주차의 달린 거리
					borderColor: 'rgba(153, 102, 255, 1)', // 라인 색상
					fill: false,
					tension: 0.1
				},
				{
					label: 'Total Stretch(min)',
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
						text: 'weekly'
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
			labels: ['Walk', 'Stretch', 'Run'], // 운동 종류
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
			labels: [0, 1, 2, 3],
			datasets: [{
				label: 'Weight variation (kg)',
				data: weights, // 각 주차의 달린 거리
				backgroundColor: 'rgba(0, 0, 128, 0.2)', // 남색 배경
				borderColor: 'rgba(0, 0, 128, 1)',
				borderWidth: 1
			}]
		},
		options: {
			responsive: true,
			scales: {
				x: {
					title: {
						display: true,
						text: 'recent'
					}
				},
				y: {
					title: {
						display: true,
						text: 'Weight(kg)'
					}
				}
			}
		}
	});


});