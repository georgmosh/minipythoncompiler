def g(a, k = 1):
	return a - k

def f(x):
	return x

x = 5
print f(g(x) + 3.0)
x = 2.0 - f(2)
print g(x)