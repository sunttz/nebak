package usi.sys.key.util;

import java.util.concurrent.locks.ReentrantLock;

public class SingleSequence {
	private final ReentrantLock lock = new ReentrantLock(false);
	
	protected long currVal = 0L;

	protected long maxVal = 0L;

	private final int cacheNum;

	private final UniqueTableApp app;
	
	//private final TransactionTemplate transactionTemplate;

	public SingleSequence(int cacheNum, UniqueTableApp app) {
		this.cacheNum = cacheNum;
		this.app = app;
		//this.transactionTemplate = transactionTemplate;
	}

	public long getNextVal(String name) {
		try {
			lock.lock();
			if (this.currVal < this.maxVal) {
				return (this.currVal++);
			}
			CacheValue cache = getNewValFromDB(name);
			this.currVal = cache.getMinVal();
			this.maxVal = cache.getMaxVal();
			return (this.currVal++);
		} finally {
			lock.unlock();
		}
	}

	private CacheValue getNewValFromDB(final String name) {
//		transactionTemplate.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRES_NEW);
//		return transactionTemplate.execute(new TransactionCallback<CacheValue>() {
//			@Override
//			public CacheValue doInTransaction(TransactionStatus status) {
//				return app.getCacheValue(cacheNum, name);
//			}
//		});
		return app.getCacheValue(cacheNum, name);
	}
}