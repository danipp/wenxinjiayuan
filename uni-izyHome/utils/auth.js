let token = '_token';

export function setToken(newToken) {
	uni.setStorageSync(token, newToken);
}

export function getToken() {
	return uni.getStorageSync(token);
}

export function removeToken() {
	uni.removeStorageSync(token);
}