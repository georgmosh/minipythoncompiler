def g(a = "world"):
	return "hello " + a

def h(a, k = 1):
	return 5 + a

def f(x):
	return x

x = "Greeting"
x = x + f(g("George") + " from compiler")
print f(x)