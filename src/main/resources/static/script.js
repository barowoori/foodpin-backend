// owner 딥링크 처리 스크립트

document.addEventListener("DOMContentLoaded", () => {
  // URL 쿼리에서 truckId 파라미터를 가져온다
  const params = new URLSearchParams(window.location.search);
  const truckId = params.get("truckId");

  // truckId가 없으면 에러 메시지 출력 후 종료
  if (!truckId) {
    document.body.innerHTML = `
      <p style="text-align:center; margin-top:50px;">
        🚨 truckId 파라미터가 필요합니다.
      </p>`;
    return;
  }

  // 딥링크 생성 및 즉시 이동 시도
  const deepLink = `foodpin://owner?truckId=${encodeURIComponent(truckId)}`;
  window.location.href = deepLink;

  // 1초 후에도 이동되지 않으면, 클릭용 링크 추가
  setTimeout(() => {
    const fallbackLink = document.createElement("a");
    fallbackLink.href = deepLink;
    fallbackLink.textContent = "푸드핀 앱으로 연결되지 않으면 여기를 클릭";
    fallbackLink.style.display = "block";
    fallbackLink.style.textAlign = "center";
    fallbackLink.style.marginTop = "20px";
    fallbackLink.style.color = "#4285f4";
    fallbackLink.style.textDecoration = "underline";
    document.body.appendChild(fallbackLink);
  }, 1000);
});
