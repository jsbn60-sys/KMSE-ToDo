<template id="user-tasks">
    <app-frame>
        <h2>{{username}}</h2>
        <ul class="task-overview-list">
            <li v-for="task in tasks">
                <dl v-if="task">
                    <dt>Title</dt>
                    <dd>{{task.id}}</dd>
                    <dt>Completed</dt>
                    <dd>{{task.title}}</dd>
                    <dt>Priority</dt>
                    <dd>{{task.done}}</dd>
                </dl>
            </li>
        </ul>

    </app-frame>
</template>
<script>
    Vue.component("user-tasks", {
        template: "#user-tasks",
        data: () => ({
            username: localStorage.getItem("username"),
            tasks: [],
        }),
        created() {
            const token = localStorage.getItem("token");
            fetch("/api/me/tasks?token=" + token, {
                method: "GET",
            })
                .then( async res => {
                    if(res.status === 200) {
                        let resdata = await res.json();
                        console.log(resdata);
                        this.tasks = resdata;
                        console.log(this.tasks)
                    }
                })
                .catch(() => alert("Error while fetching tasks"));
        }
    });
</script>
