/**
 * 获取数组第一个元素，没有则返回 null
 */
declare global {
	interface Array<T> {
		firstOrNull(): T | null;
	}
}

Array.prototype.firstOrNull = function <T>(this: T[]): T | null {
	return this.length > 0 ? this[0] : null;
};

export {};
