let selectedTableId = null;
window._sessionCache = {};
let modalTimer = null;
let deletingTableId = null;

const token = localStorage.getItem("token");
const roles = JSON.parse(localStorage.getItem("roles") || "[]");

if (!token) {
    window.location.href = "/login";
}

window.currentRole = roles.includes("ROLE_ADMIN")
    ? "ROLE_ADMIN"
    : roles.includes("ROLE_MANAGER")
        ? "ROLE_MANAGER"
        : "ROLE_STAFF";

function authHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    };
}

function isAdmin() {
    return window.currentRole === "ROLE_ADMIN";
}

function isManager() {
    return window.currentRole === "ROLE_MANAGER";
}

function applyRolePermission() {
    if (!isAdmin()) {
        document.querySelectorAll(".admin-only")
            .forEach(element => element.remove());
    }
}

function updateSummary() {
    const cards = document.querySelectorAll(".billiard-table-card");

    let available = 0;
    let playing = 0;
    let paused = 0;

    cards.forEach(card => {
        const status = card.dataset.status;

        if (status === "AVAILABLE") available++;
        if (status === "OCCUPIED") playing++;
        if (status === "PAUSED") paused++;
    });

    document.getElementById("availableCount").innerText = available;
    document.getElementById("playingCount").innerText = playing;
    document.getElementById("pausedCount").innerText = paused;
}

function formatTime(seconds) {
    seconds = Number(seconds);

    if (isNaN(seconds) || seconds < 0) {
        seconds = 0;
    }

    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = Math.floor(seconds % 60);

    return String(h).padStart(2, "0") + ":" +
        String(m).padStart(2, "0") + ":" +
        String(s).padStart(2, "0");
}

function formatMoney(amount) {
    return Math.floor(amount).toLocaleString("vi-VN");
}

function getTableTypeText(type) {
    if (type === "CAROM") return "Bida libre / 3 bi";
    if (type === "SNOOKER") return "Snooker";
    if (type === "VIP_POOL") return "Bida lỗ VIP";
    if (type === "VIP_SNOOKER") return "Snooker VIP";
    return "Bida lỗ";
}

window.openTableModal = async function (element) {
    selectedTableId = element.dataset.tableId;

    // ✅ FIX: chỉ lấy cache 1 lần
    currentSessionId = window._sessionCache[selectedTableId] || null;

    clearInterval(modalTimer);

    document.getElementById("modalTableName").innerText = element.dataset.tableName;
    document.getElementById("modalTableCode").innerText = element.dataset.tableCode;
    document.getElementById("modalStatus").innerText = element.dataset.status;

    if (document.getElementById("modalTableType")) {
        document.getElementById("modalTableType").innerText =
            getTableTypeText(element.dataset.tableType);
    }

    const ratePerMinute = Number(element.dataset.ratePerMinute);

    document.getElementById("modalPrice").innerText =
        (ratePerMinute * 60).toLocaleString("vi-VN");

    document.getElementById("modalDate").innerText = "--";
    document.getElementById("modalStartTime").innerText = "--";
    document.getElementById("modalPlayingTime").innerText = "00:00:00";
    document.getElementById("modalClock").innerText = "00:00:00";
    document.getElementById("modalEstimatedPrice").innerText = "0";

    const response = await fetch(
        "/api/sessions/table/" + selectedTableId + "/active",
        {
            headers: authHeaders()
        }
    );

    // ❗ nếu không có session → clear cache
    window._sessionCache[selectedTableId] = null;
    currentSessionId = null;

    if (response.ok) {
        const text = await response.text();

        if (text) {
            const session = JSON.parse(text);

            if (session && session.id) {
                currentSessionId = session.id;

                // ✅ FIX: cache session theo table
                window._sessionCache[selectedTableId] = {
                    sessionId: session.id,
                    startTime: session.startTime
                };

                const startTime = new Date(session.startTime);

                document.getElementById("modalDate").innerText =
                    startTime.toLocaleDateString("vi-VN");

                document.getElementById("modalStartTime").innerText =
                    startTime.toLocaleTimeString("vi-VN");

                async function updateModalClock() {
                    const secondsResponse = await fetch(
                        "/api/sessions/" + session.id + "/playing-seconds",
                        {
                            headers: authHeaders()
                        }
                    );

                    let seconds = 0;

                    if (secondsResponse.ok) {
                        seconds = Number(await secondsResponse.text());
                    }

                    const formatted = formatTime(seconds);
                    const estimated = (seconds / 60) * ratePerMinute;

                    document.getElementById("modalPlayingTime").innerText = formatted;
                    document.getElementById("modalClock").innerText = formatted;
                    document.getElementById("modalEstimatedPrice").innerText =
                        formatMoney(estimated);
                }

                updateModalClock();
                modalTimer = setInterval(updateModalClock, 1000);
            }
        }
    }

    const modal = new bootstrap.Modal(document.getElementById("tableModal"));
    modal.show();
};

window.openCreateTableForm = function () {
    document.getElementById("tableFormTitle").innerText = "Thêm bàn";

    document.getElementById("formTableId").value = "";
    document.getElementById("formTableCode").value = "";
    document.getElementById("formTableName").value = "";
    document.getElementById("formRatePerMinute").value = "";

    document.querySelector("input[name='tableType'][value='POOL']").checked = true;

    const modal = new bootstrap.Modal(document.getElementById("tableFormModal"));
    modal.show();
};

window.openEditTableForm = function (element) {
    document.getElementById("tableFormTitle").innerText = "Sửa bàn";

    document.getElementById("formTableId").value = element.dataset.tableId;
    document.getElementById("formTableCode").value = element.dataset.tableCode;
    document.getElementById("formTableName").value = element.dataset.tableName;
    document.getElementById("formRatePerMinute").value = element.dataset.ratePerMinute;

    const type = element.dataset.tableType || "POOL";
    document.querySelector(`input[name='tableType'][value='${type}']`).checked = true;

    const modal = new bootstrap.Modal(document.getElementById("tableFormModal"));
    modal.show();
};

window.saveTable = async function () {
    if (!isAdmin()) {
        alert("Bạn không có quyền thêm/sửa bàn");
        return;
    }

    const tableId = document.getElementById("formTableId").value;

    const body = {
        tableCode: document.getElementById("formTableCode").value.trim(),
        name: document.getElementById("formTableName").value.trim(),
        ratePerMinute: Number(document.getElementById("formRatePerMinute").value),
        tableType: document.querySelector("input[name='tableType']:checked").value
    };

    if (!body.tableCode || !body.name || !body.ratePerMinute) {
        alert("Vui lòng nhập đầy đủ thông tin bàn");
        return;
    }

    const url = tableId ? "/api/tables/" + tableId : "/api/tables";
    const method = tableId ? "PUT" : "POST";

    const response = await fetch(url, {
        method: method,
        headers: authHeaders(),
        body: JSON.stringify(body)
    });

    if (!response.ok) {
        const errorText = await response.text();
        alert("Lưu bàn thất bại: " + errorText);
        return;
    }

    location.reload();
};

window.deleteTable = function (tableId) {
    if (!isAdmin()) {
        alert("Bạn không có quyền xóa bàn");
        return;
    }

    deletingTableId = tableId;

    const modal = new bootstrap.Modal(
        document.getElementById("deleteConfirmModal")
    );

    modal.show();
};

function initTableClocks() {
    document.querySelectorAll(".billiard-table-card").forEach(async card => {
        const status = card.dataset.status;
        const clock = card.querySelector(".live-clock");
        const tableId = card.dataset.tableId;

        if (status === "OCCUPIED" || status === "PAUSED") {
            const response = await fetch(
                "/api/sessions/table/" + tableId + "/active",
                {
                    headers: authHeaders()
                }
            );

            const text = await response.text();

            if (text) {
                const session = JSON.parse(text);

                const secondsResponse = await fetch(
                    "/api/sessions/" + session.id + "/playing-seconds",
                    {
                        headers: authHeaders()
                    }
                );

                let seconds = 0;

                if (secondsResponse.ok) {
                    seconds = Number(await secondsResponse.text());
                }

                clock.innerText = formatTime(seconds);

                if (status === "OCCUPIED") {
                    setInterval(async () => {
                        const secondsResponse = await fetch(
                            "/api/sessions/" + session.id + "/playing-seconds",
                            {
                                headers: authHeaders()
                            }
                        );

                        seconds = Number(await secondsResponse.text());
                        clock.innerText = formatTime(seconds);
                    }, 1000);
                }
            }
        } else {
            clock.innerText = "00:00:00";
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    applyRolePermission();
    updateSummary();
    initTableClocks();

    const openBtn = document.getElementById("openBtn");
    if (openBtn) {
        openBtn.onclick = async () => {
            const response = await fetch("/api/sessions/open/" + selectedTableId, {
                method: "POST",
                headers: authHeaders()
            });

            if (!response.ok) {
                const errorText = await response.text();
                alert("Mở phiên thất bại: " + errorText);
                return;
            }

            const sessionId = await response.text();
            currentSessionId = Number(sessionId);

            // ✅ FIX: sync cache luôn
            window._sessionCache[selectedTableId] = currentSessionId;

            document.getElementById("modalStatus").innerText = "OCCUPIED";

            alert("Mở phiên thành công");
        };
    }

    const openPosBtn = document.getElementById("openPosBtn");



    const pauseBtn = document.getElementById("pauseBtn");
    if (pauseBtn) {
        pauseBtn.onclick = () => {
            if (!currentSessionId) {
                alert("Bàn này chưa có phiên chơi");
                return;
            }

            fetch("/api/sessions/" + currentSessionId + "/pause", {
                method: "POST",
                headers: authHeaders()
            }).then(() => location.reload());
        };
    }

    const resumeBtn = document.getElementById("resumeBtn");
    if (resumeBtn) {
        resumeBtn.onclick = () => {
            if (!currentSessionId) {
                alert("Bàn này chưa có phiên chơi");
                return;
            }

            fetch("/api/sessions/" + currentSessionId + "/resume", {
                method: "POST",
                headers: authHeaders()
            }).then(() => location.reload());
        };
    }

    const endBtn = document.getElementById("endBtn");
    if (endBtn) {
        endBtn.onclick = () => {
            if (!currentSessionId) {
                alert("Bàn này chưa có phiên chơi");
                return;
            }

            fetch("/api/sessions/" + currentSessionId + "/end", {
                method: "POST",
                headers: authHeaders()
            }).then(() => location.reload());
        };
    }

    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");

    if (confirmDeleteBtn) {
        confirmDeleteBtn.onclick = async () => {
            if (!deletingTableId) return;

            const response = await fetch("/api/tables/" + deletingTableId, {
                method: "DELETE",
                headers: authHeaders()
            });

            if (!response.ok) {
                const errorText = await response.text();
                alert("Không thể xóa bàn: " + errorText);
                return;
            }

            location.reload();
        };
    }
});