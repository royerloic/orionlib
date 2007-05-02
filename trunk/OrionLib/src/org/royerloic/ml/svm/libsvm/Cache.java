/*
 * Created on 04.10.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.royerloic.ml.svm.libsvm;

//
// Kernel Cache
//
// mNumberOfSupportVectors is the number of total data items
// size is the cache size limit in bytes
//
class Cache
{
	private final int	l;

	private int				size;

	private final class head_t
	{
		head_t	prev, next; // a cicular list

		float[]	data;

		int			len;				// data[0,len) is cached in this entry
	}

	private final head_t[]	head;

	private head_t					lru_head;

	Cache(final int l_, final int size_)
	{
		this.l = l_;
		this.size = size_;
		this.head = new head_t[this.l];
		for (int i = 0; i < this.l; i++)
			this.head[i] = new head_t();
		this.size /= 4;
		this.size -= this.l * (16 / 4); // sizeof(head_t) == 16
		this.lru_head = new head_t();
		this.lru_head.next = this.lru_head.prev = this.lru_head;
	}

	private void lru_delete(final head_t h)
	{
		// delete from current location
		h.prev.next = h.next;
		h.next.prev = h.prev;
	}

	private void lru_insert(final head_t h)
	{
		// insert to last position
		h.next = this.lru_head;
		h.prev = this.lru_head.prev;
		h.prev.next = h;
		h.next.prev = h;
	}

	// request data [0,len)
	// return some position p where [p,len) need to be filled
	// (p >= len if nothing needs to be filled)
	// java: simulate pointer using single-element array
	int get_data(final int index, final float[][] data, int len)
	{
		final head_t h = this.head[index];
		if (h.len > 0)
			lru_delete(h);
		final int more = len - h.len;

		if (more > 0)
		{
			// free old space
			while (this.size < more)
			{
				final head_t old = this.lru_head.next;
				lru_delete(old);
				this.size += old.len;
				old.data = null;
				old.len = 0;
			}

			// allocate new space
			final float[] new_data = new float[len];
			if (h.data != null)
				System.arraycopy(h.data, 0, new_data, 0, h.len);
			h.data = new_data;
			this.size -= more;
			do
			{
				final int _ = h.len;
				h.len = len;
				len = _;
			}
			while (false);
		}

		lru_insert(h);
		data[0] = h.data;
		return len;
	}

	void swap_index(int i, int j)
	{
		if (i == j)
			return;

		if (this.head[i].len > 0)
			lru_delete(this.head[i]);
		if (this.head[j].len > 0)
			lru_delete(this.head[j]);
		do
		{
			final float[] _ = this.head[i].data;
			this.head[i].data = this.head[j].data;
			this.head[j].data = _;
		}
		while (false);
		do
		{
			final int _ = this.head[i].len;
			this.head[i].len = this.head[j].len;
			this.head[j].len = _;
		}
		while (false);
		if (this.head[i].len > 0)
			lru_insert(this.head[i]);
		if (this.head[j].len > 0)
			lru_insert(this.head[j]);

		if (i > j)
			do
			{
				final int _ = i;
				i = j;
				j = _;
			}
			while (false);
		for (head_t h = this.lru_head.next; h != this.lru_head; h = h.next)
			if (h.len > i)
				if (h.len > j)
					do
					{
						final float _ = h.data[i];
						h.data[i] = h.data[j];
						h.data[j] = _;
					}
					while (false);
				else
				{
					// give up
					lru_delete(h);
					this.size += h.len;
					h.data = null;
					h.len = 0;
				}
	}
}