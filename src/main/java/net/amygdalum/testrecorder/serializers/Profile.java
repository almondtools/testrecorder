package net.amygdalum.testrecorder.serializers;

public class Profile implements Comparable<Profile> {

	public long start;
	public long duration;
	public Class<?> type;

	private Profile(Class<?> type) {
		this.type = type;
		this.duration = Long.MAX_VALUE;
	}

	public static Profile start(Class<?> type) {
		Profile serialization = new Profile(type);
		serialization.start = System.currentTimeMillis();
		return serialization;
	}

	public Profile stop() {
		this.duration = System.currentTimeMillis() - start;
		return this;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public int compareTo(Profile that) {
		int compare = Long.compare(that.duration, this.duration);
		if (compare == 0) {
			compare = Long.compare(this.start, that.start);
		}
		return compare;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Profile that = (Profile) obj;
		return this.type.equals(that.type);
	}

	@Override
	public String toString() {
		return type.getName() + ":" + (duration == Long.MAX_VALUE ? "timeout" : duration);
	}

}
