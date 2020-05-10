<template id="user-login">
    <app-frame>
        <input v-model="username" placeholder="insert username here">
        <input type="password" v-model="password" placeholder="insert password here">
        <button v-on:click="login()">Login</button>
    </app-frame>
</template>
<script>
    Vue.component("user-login", {
        template: "#user-login",
        data: () => ({
            username: "",
            password: ""
        }),
        methods: {
            login() {
                console.log(this.username)
                let username = this.username;
                let password = this.password;
                fetch("/api/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "username=" + username + "&password=" + password
                })
                    .then(async res => { if(res.status === 200) {
                        const data = await res.json();
                        localStorage.username = this.username;
                        localStorage.token = data;
                        location.pathname = "/tasks"
                    }})
                    .catch(() => alert("Error while login"));
                console.log("login")
            }
        },
    });
</script>
